package com.stellarbitsapps.androidpdv.ui.finalcash

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.databinding.FragmentFinalCashBinding
import com.stellarbitsapps.androidpdv.util.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

class FinalCashFragment : Fragment() {

    companion object {
        fun newInstance() = FinalCashFragment()
    }

    private val viewModel: FinalCashViewModel by activityViewModels {
        FinalCashViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.reportDao(),
            (requireActivity().application as AndroidPdvApplication).database.sangriaDao(),
            (requireActivity().application as AndroidPdvApplication).database.reportErrorDao()
        )
    }

    private val binding: FragmentFinalCashBinding by lazy {
        FragmentFinalCashBinding.inflate(layoutInflater)
    }

    private lateinit var printHelper: AP80PrintHelper

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        printHelper = AP80PrintHelper.getInstance()
        printHelper.initPrint(requireContext())

        binding.edtFinalCash.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Handler().postDelayed({
                    binding.edtFinalCash.setSelection(binding.edtFinalCash.length())
                }, 1)
            }
        }

        binding.edtFinalCash.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Utils.formatCashTextMask(s, binding.edtFinalCash, this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })

        binding.btFinish.setOnClickListener {
            printReport()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    @SuppressLint("SimpleDateFormat")
    private fun printReport() {
        viewModel.getErrors()
        viewModel.error.observe(viewLifecycleOwner) { error ->

            viewModel.getSangria()
            viewModel.sangria.observe(viewLifecycleOwner) { sangria ->

                viewModel.getReport()
                viewModel.report.observe(viewLifecycleOwner) { report ->

                    // ------------------------- DATE ------------------------- //

                    val calendar = Calendar.getInstance()
                    val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

                    val initialDate = format.format(report.initialDate)
                    val finalDate = format.format(calendar.time)

                    // ------------------------- DATE ------------------------- //

                    // ------------------------- CALC ------------------------- //

                    // Cash Register
                    val initialValue = report.initialCash
                    val finalValue =
                        if (binding.edtFinalCash.text.toString().isEmpty()) 0f else {
                            binding.edtFinalCash.text.toString()
                                .replace("R$", "")
                                .replace(".", "")
                                .replace(",", ".")
                                .trim()
                                .toFloat()
                        }

                    // Tokens
                    val formattedTokensOneSold = report.cashOneTokensSold.toString().padEnd(6 - (report.cashOneTokensSold.toString().length + 1), ' ')
                    val formattedTokensTwoSold = report.cashTwoTokensSold.toString().padEnd(6 - (report.cashTwoTokensSold.toString().length + 1), ' ')
                    val formattedTokensFourSold = report.cashFourTokensSold.toString().padEnd(6 - (report.cashFourTokensSold.toString().length + 1), ' ')
                    val formattedTokensFiveSold = report.cashFiveTokensSold.toString().padEnd(6 - (report.cashFiveTokensSold.toString().length + 1), ' ')
                    val formattedTokensSixSold = report.cashSixTokensSold.toString().padEnd(6 - (report.cashSixTokensSold.toString().length + 1), ' ')
                    val formattedTokensEightSold = report.cashEightTokensSold.toString().padEnd(6 - (report.cashEightTokensSold.toString().length + 1), ' ')
                    val formattedTokensTenSold = report.cashTenTokensSold.toString().padEnd(6 - (report.cashTenTokensSold.toString().length + 1), ' ')

                    val tokensOneSold = report.cashOneTokensSold
                    val tokensTwoSold = report.cashTwoTokensSold
                    val tokensFourSold = report.cashFourTokensSold
                    val tokensFiveSold = report.cashFiveTokensSold
                    val tokensSixSold = report.cashSixTokensSold
                    val tokensEightSold = report.cashEightTokensSold
                    val tokensTenSold = report.cashTenTokensSold

                    val totalTokensOne = tokensOneSold.toFloat()
                    val totalTokensTwo = 2 * tokensTwoSold.toFloat()
                    val totalTokensFour = 4 * tokensFourSold.toFloat()
                    val totalTokensFive = 5 * tokensFiveSold.toFloat()
                    val totalTokensSix = 6 * tokensSixSold.toFloat()
                    val totalTokensEight = 8 * tokensEightSold.toFloat()
                    val totalTokensTen = 10 * tokensTenSold.toFloat()

                    val tokensTotal = totalTokensOne + totalTokensTwo + totalTokensFour + totalTokensFive + totalTokensSix + totalTokensEight + totalTokensTen

                    // Sangria
                    val sangriaSum = sangria.sumOf { it.sangria.toDouble() }.toFloat()

                    // Errors
                    val errorSum = error.sumOf { it.error.toDouble() }.toFloat()

                    // Balance
                    val balance = initialValue + tokensTotal - sangriaSum // Abertura + Vendas - Sangrias

                    // ------------------------- CALC ------------------------- //

                    printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
                    printSpace(1)
                    printHelper.printData(Utils.getDeviceName(), 70, 1, false, 1, 80, 1)
                    printSpace(1)
                    printHelper.printData("Abertura:\n$initialDate - R$ ${String.format("%.2f", initialValue)}", 30, 0, false, 0, 80, 0)
                    printSpace(1)
                    printHelper.printData("Fechamento:\n$finalDate - R$ ${String.format("%.2f", finalValue)}", 30, 0, false, 0, 80, 0)
                    printSpace(1)
                    printHelper.printData("Saldo (Abertura + Vendas - Sangrias):\nR$ ${String.format("%.2f", balance)}", 30, 0, false, 0, 80, 0)
                    printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
                    printSpace(1)
                    printHelper.printData("R$ 1,00  - Qtde x $formattedTokensOneSold - Total R$: ${String.format("%.2f", totalTokensOne)}", 26, 0, false, 0, 80, 0)
                    printHelper.printData("R$ 2,00  - Qtde x $formattedTokensTwoSold - Total R$: ${String.format("%.2f", totalTokensTwo)}", 26, 0, false, 0, 80, 0)
                    printHelper.printData("R$ 4,00  - Qtde x $formattedTokensFourSold - Total R$: ${String.format("%.2f", totalTokensFour)}", 26, 0, false, 0, 80, 0)
                    printHelper.printData("R$ 5,00  - Qtde x $formattedTokensFiveSold - Total R$: ${String.format("%.2f", totalTokensFive)}", 26, 0, false, 0, 80, 0)
                    printHelper.printData("R$ 6,00  - Qtde x $formattedTokensSixSold - Total R$: ${String.format("%.2f", totalTokensSix)}", 26, 0, false, 0, 80, 0)
                    printHelper.printData("R$ 8,00  - Qtde x $formattedTokensEightSold - Total R$: ${String.format("%.2f", totalTokensEight)}", 26, 0, false, 0, 80, 0)
                    printHelper.printData("R$ 10,00 - Qtde x $formattedTokensTenSold - Total R$: ${String.format("%.2f", totalTokensTen)}", 26, 0, false, 0, 80, 0)
                    printSpace(1)
                    printHelper.printData("Total de vendas R$: ${String.format("%.2f", tokensTotal)}", 30, 0, false, 0, 80, 1)
                    printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
                    printSpace(1)
                    printHelper.printData("Total Dinheiro ..... R$: ${String.format("%.2f", report.paymentCash)}", 30, 0, false, 0, 80, 0)
                    printHelper.printData("Total Pix .......... R$: ${String.format("%.2f", report.paymentPix)}", 30, 0, false, 0, 80, 0)
                    printHelper.printData("Total Débito ....... R$: ${String.format("%.2f", report.paymentDebit)}", 30, 0, false, 0, 80, 0)
                    printHelper.printData("Total Crédito ...... R$: ${String.format("%.2f", report.paymentCredit)}", 30, 0, false, 0, 80, 0)
                    printHelper.printData("Abertura do caixa .. R$: ${String.format("%.2f", report.initialCash)}", 30, 0, false, 0, 80, 0)
                    printHelper.printData("Total Geral ........ R$: ${String.format("%.2f", report.paymentCash + report.paymentPix + report.paymentDebit + report.paymentCredit + report.initialCash)}", 30, 0, false, 0, 80, 0)
                    printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)

                    // Sangria
                    printSpace(1)
                    printHelper.printData("Sangria:", 30, 0, false, 0, 80, 0)

                    sangria.forEach {
                        val date = format.format(it.date)
                        val text = "$date - R$: ${String.format("%.2f", it.sangria)}"
                        printHelper.printData(text, 30, 0, false, 0, 80, 0)
                    }

                    printSpace(1)
                    printHelper.printData("Total das sangrias R$: ${String.format("%.2f", sangriaSum)}", 30, 0, false, 0, 80, 0)

                    // Errors
                    printSpace(1)
                    printHelper.printData("Erros reportados:", 30, 0, false, 0, 80, 0)

                    error.forEach {
                        val date = format.format(it.date)
                        val text = "$date - R$: ${String.format("%.2f", it.error)}"
                        printHelper.printData(text, 30, 0, false, 0, 80, 0)
                    }

                    printSpace(1)
                    printHelper.printData("Total dos erros reportados R$: ${String.format("%.2f", errorSum)}", 30, 0, false, 0, 80, 0)

                    printSpace(3)
                    printHelper.printStart()
                    printHelper.cutPaper(1)

                    viewModel.reportSangria()
                    viewModel.reportErrors()
                    viewModel.closeCashRegister(finalValue, calendar)

                    findNavController().navigate(R.id.initialCashFragment)
                }
            }
        }
    }

    private fun printSpace(spaceSize: Int) {
        if (spaceSize < 0) {
            return
        }
        val strSpace = StringBuilder()
        for (i in 0 until spaceSize) {
            strSpace.append("\n")
        }
        printHelper.printData(strSpace.toString(), 32, 0, false, 1, 80, 0)
    }
}