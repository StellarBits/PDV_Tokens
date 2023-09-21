package com.stellarbitsapps.androidpdv.ui.tokens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
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
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.Report
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
            (requireActivity().application as AndroidPdvApplication).database.reportDao()
        )
    }

    private val binding: FragmentTokensBinding by lazy {
        FragmentTokensBinding.inflate(layoutInflater)
    }

    private lateinit var recyclerView: RecyclerView

    private var tokenSum = 0f

    private var selectedTokensList = arrayListOf<Tokens>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initRecyclerView()

        binding.btClean.setOnClickListener {
            clearFields()
        }

        binding.btExit.setOnClickListener {
            val direction = TokensFragmentDirections.actionTokensFragmentToFinalCashFragment()
            findNavController().navigate(direction)
        }

        binding.btCash.setOnClickListener {
            // Update payment method in database report table
            val reportToBeUpdated = Report(paymentMethodCash = 1)
            viewModel.updateReportTokens(reportToBeUpdated)

            Utils.tokenPayment(viewModel, selectedTokensList)

            showCashChangeDialog()
        }

        binding.btPix.setOnClickListener {
            // Update payment method in database report table
            val reportToBeUpdated = Report(paymentMethodPix = 1)
            viewModel.updateReportTokens(reportToBeUpdated)

            Utils.tokenPayment(viewModel, selectedTokensList)

            clearFields()
        }

        binding.btDebit.setOnClickListener {
            // Update payment method in database report table
            val reportToBeUpdated = Report(paymentMethodDebit = 1)
            viewModel.updateReportTokens(reportToBeUpdated)

            Utils.tokenPayment(viewModel, selectedTokensList)

            clearFields()
        }

        binding.btCredit.setOnClickListener {
            // Update payment method in database report table
            val reportToBeUpdated = Report(paymentMethodCredit = 1)
            viewModel.updateReportTokens(reportToBeUpdated)

            Utils.tokenPayment(viewModel, selectedTokensList)

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
        lifecycle.coroutineScope.launch {
            viewModel.getTokens().collect {
                tokensAdapter.submitList(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showCashChangeDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogLayout: View =
            inflater.inflate(R.layout.cash_change_dialog_layout, requireActivity().findViewById(R.id.token_layout) as ViewGroup?)
        val db = AlertDialog.Builder(requireContext())

        val amountReceivedEditText = dialogLayout.findViewById<View>(R.id.edt_amount_received) as EditText
        val calcCashChangeButton = dialogLayout.findViewById<View>(R.id.bt_calc_cash_change) as TextView
        val totalCashChangeTextView = dialogLayout.findViewById<View>(R.id.tv_total_cash_change) as TextView

        amountReceivedEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Utils.formatCashTextMask(s, amountReceivedEditText, this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun afterTextChanged(s: Editable) { }
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

        db.setView(dialogLayout)
        db.setTitle("Digite o valor recebido")
        db.setPositiveButton("OK") { _, _ -> clearFields() }
        db.show()
    }

    private fun clearFields() {
        binding.tvTotaValue.text = "R$ 0,00"
        tokenSum = 0f
        selectedTokensList.clear()
    }
}