package com.stellarbitsapps.androidpdv.ui.tokens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.databinding.FragmentTokensBinding
import com.stellarbitsapps.androidpdv.ui.adapter.TokensAdapter
import com.stellarbitsapps.androidpdv.util.Utils


class TokensFragment : Fragment() {

    companion object {
        fun newInstance() = TokensFragment()
    }

    private val viewModel: TokensViewModel by activityViewModels {
        TokensViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.tokensDao(),
            (requireActivity().application as AndroidPdvApplication).database.reportDao(),
            (requireActivity().application as AndroidPdvApplication).database.layoutSettingsDao()
        )
    }

    private val binding: FragmentTokensBinding by lazy {
        FragmentTokensBinding.inflate(layoutInflater)
    }

    private lateinit var tokensAdapter: TokensAdapter

    private lateinit var recyclerView: RecyclerView

    private var tokenSum = 0f

    private var selectedTokensList = arrayListOf<Tokens>()

    private var tokenSettings = LayoutSettings()

    private lateinit var printHelper: AP80PrintHelper

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        printHelper = AP80PrintHelper.getInstance()
        printHelper.initPrint(requireContext())

        initRecyclerView()

        loadTokenLayoutSettings()

        binding.btClean.setOnClickListener {
            clearFields()
        }

        binding.btExit.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Atenção!")
                .setMessage("Você realmente deseja sair para fechar o caixa?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sim") { _, _ ->
                    findNavController().navigate(R.id.finalCashFragment)
                }
                .setNegativeButton("Não", null).show()
        }

        binding.btCash.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val tokenValues = arrayOf(tokenSum, 0f, 0f, 0f)
            Utils.tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, printHelper, this)

            showCashChangeDialog()
        }

        binding.btPix.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val tokenValues = arrayOf(0f, tokenSum, 0f, 0f)
            Utils.tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, printHelper, this)
        }

        binding.btDebit.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val tokenValues = arrayOf(0f, 0f, tokenSum, 0f)
            Utils.tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, printHelper, this)
        }

        binding.btCredit.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val tokenValues = arrayOf(0f, 0f, 0f, tokenSum)
            Utils.tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, printHelper, this)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initRecyclerView() {
        tokensAdapter = TokensAdapter(this)

        recyclerView = binding.rvCashButtons

        recyclerView.adapter = tokensAdapter

        viewModel.getTokens()
        viewModel.tokensList.observe(viewLifecycleOwner) {
            tokensAdapter.submitList(it)
        }
    }

    private fun loadTokenLayoutSettings() {
        viewModel.getConfigs()
        viewModel.layoutSettings.observe(viewLifecycleOwner) {
            tokenSettings = it
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showCashChangeDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogLayout: View =
            inflater.inflate(
                R.layout.cash_change_dialog_layout,
                requireActivity().findViewById(R.id.token_layout) as ViewGroup?
            )
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        val amountReceivedEditText =
            dialogLayout.findViewById<View>(R.id.edt_amount_received) as EditText
        val calcCashChangeButton =
            dialogLayout.findViewById<View>(R.id.bt_calc_cash_change) as TextView
        val totalCashChangeTextView =
            dialogLayout.findViewById<View>(R.id.tv_total_cash_change) as TextView

        amountReceivedEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Handler().postDelayed({
                    amountReceivedEditText.setSelection(amountReceivedEditText.length())
                }, 1)
            }
        }

        amountReceivedEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Utils.formatCashTextMask(s, amountReceivedEditText, this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })

        calcCashChangeButton.setOnClickListener {
            val amountReceived = amountReceivedEditText.text.toString()
                .replace("R$", "")
                .replace(",", ".")
                .trim()
                .toFloat()
            val cashChange = amountReceived - tokenSum

            totalCashChangeTextView.text = "Troco: R$ " + String.format("%.2f", cashChange)
        }

        alertDialogBuilder.setView(dialogLayout)
        alertDialogBuilder.setTitle("Digite o valor recebido (Total: R$ ${String.format("%.2f", tokenSum)})")
        alertDialogBuilder.setPositiveButton("OK") { _, _ -> }
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearFields() {
        binding.tvTotaValue.text = "R$ 0,00"
        tokenSum = 0f
        selectedTokensList.clear()

        tokensAdapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    fun tokenClicked(token: Tokens) {
        selectedTokensList.add(token)
        tokenSum += token.value
        binding.tvTotaValue.text = "R$ " + String.format("%.2f", tokenSum)
    }
}