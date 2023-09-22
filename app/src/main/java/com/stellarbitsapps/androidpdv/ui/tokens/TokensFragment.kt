package com.stellarbitsapps.androidpdv.ui.tokens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.databinding.FragmentTokensBinding
import com.stellarbitsapps.androidpdv.ui.adapter.TokensAdapter
import com.stellarbitsapps.androidpdv.ui.adapter.TokensListener
import com.stellarbitsapps.androidpdv.util.Utils
import kotlinx.coroutines.launch

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
            findNavController().navigate(R.id.finalCashFragment)
        }

        binding.btCash.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val paymentMethodArray = arrayOf(1, 0, 0, 0)
            Utils.tokenPayment(viewModel, tokenSettings, paymentMethodArray, selectedTokensList, printHelper, this)

            showCashChangeDialog()
        }

        binding.btPix.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val paymentMethodArray = arrayOf(0, 1, 0, 0)
            Utils.tokenPayment(viewModel, tokenSettings, paymentMethodArray, selectedTokensList, printHelper, this)

            clearFields()
        }

        binding.btDebit.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val paymentMethodArray = arrayOf(0, 0, 1, 0)
            Utils.tokenPayment(viewModel, tokenSettings, paymentMethodArray, selectedTokensList, printHelper, this)

            clearFields()
        }

        binding.btCredit.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val paymentMethodArray = arrayOf(0, 0, 0, 1)
            Utils.tokenPayment(viewModel, tokenSettings, paymentMethodArray, selectedTokensList, printHelper, this)

            clearFields()
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initRecyclerView() {
        val tokensAdapter = TokensAdapter(TokensListener(
            clickListener = { token ->
                selectedTokensList.add(token)
                tokenSum += token.value
                binding.tvTotaValue.text = "R$ " + String.format("%.2f", tokenSum)
            }
        ))

        recyclerView = binding.rvCashButtons

        recyclerView.adapter = tokensAdapter

        viewModel.getTokens()
        viewModel.tokensList.observe(viewLifecycleOwner) {
            tokensAdapter.submitList(it)
        }
    }

    private fun loadTokenLayoutSettings() {
        lifecycle.coroutineScope.launch {
            viewModel.getRowsCount().collect {
                if (it > 0) {
                    viewModel.getConfigs().collect { configs ->
                        tokenSettings = configs
                    }
                }
            }
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
        alertDialogBuilder.setPositiveButton("OK") { _, _ -> clearFields() }
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.show()
    }

    private fun clearFields() {
        binding.tvTotaValue.text = "R$ 0,00"
        tokenSum = 0f
        selectedTokensList.clear()
    }
}