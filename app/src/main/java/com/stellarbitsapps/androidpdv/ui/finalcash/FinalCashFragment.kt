package com.stellarbitsapps.androidpdv.ui.finalcash

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.databinding.FragmentFinalCashBinding
import com.stellarbitsapps.androidpdv.databinding.FragmentInitialCashBinding
import com.stellarbitsapps.androidpdv.ui.initialcash.InitialCashViewModel
import com.stellarbitsapps.androidpdv.ui.initialcash.InitialCashViewModelFactory
import com.stellarbitsapps.androidpdv.util.Utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.Executors

class FinalCashFragment : Fragment() {

    companion object {
        fun newInstance() = FinalCashFragment()
    }

    private val viewModel: FinalCashViewModel by activityViewModels {
        FinalCashViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.reportDao(),
            (requireActivity().application as AndroidPdvApplication).database.sangriaDao()
        )
    }

    private val binding: FragmentFinalCashBinding by lazy {
        FragmentFinalCashBinding.inflate(layoutInflater)
    }

    private lateinit var printHelper: AP80PrintHelper

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

    @SuppressLint("SimpleDateFormat")
    private fun printReport() {
        viewModel.getSangria()
        viewModel.sangria.observe(viewLifecycleOwner) { sangria ->

            viewModel.getReport()
            viewModel.report.observe(viewLifecycleOwner) { report ->

                val calendar = Calendar.getInstance()
                val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

                val initialDate = format.format(report.initialDate)
                val finalDate = format.format(calendar.time)

                printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
                printSpace(1)
                printHelper.printData("Abertura:\nR$ ${String.format("%.2f", report.initialCash)} - $initialDate", 30, 0, false, 0, 80, 0)
                printHelper.printData("Fechamento:\n${if (binding.edtFinalCash.text.toString().isEmpty()) "R$ 0,00" else binding.edtFinalCash.text} - $finalDate", 30, 0, false, 0, 80, 0)
                printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
                printSpace(1)
                printHelper.printData("R$ 1,00  - Qtde x ${report.cashOneTokensSold} - Total R$: ${String.format("%.2f", report.cashOneTokensSold.toFloat())}", 30, 0, false, 0, 80, 0)
                printHelper.printData("R$ 2,00  - Qtde x ${report.cashTwoTokensSold} - Total R$: ${String.format("%.2f", 2 * report.cashTwoTokensSold.toFloat())}", 30, 0, false, 0, 80, 0)
                printHelper.printData("R$ 4,00  - Qtde x ${report.cashFourTokensSold} - Total R$: ${String.format("%.2f", 4 * report.cashFourTokensSold.toFloat())}", 30, 0, false, 0, 80, 0)
                printHelper.printData("R$ 5,00  - Qtde x ${report.cashFiveTokensSold} - Total R$: ${String.format("%.2f", 5 * report.cashFiveTokensSold.toFloat())}", 30, 0, false, 0, 80, 0)
                printHelper.printData("R$ 6,00  - Qtde x ${report.cashSixTokensSold} - Total R$: ${String.format("%.2f", 6 * report.cashSixTokensSold.toFloat())}", 30, 0, false, 0, 80, 0)
                printHelper.printData("R$ 8,00  - Qtde x ${report.cashEightTokensSold} - Total R$: ${String.format("%.2f", 8 * report.cashEightTokensSold.toFloat())}", 30, 0, false, 0, 80, 0)
                printHelper.printData("R$ 10,00 - Qtde x ${report.cashTenTokensSold} - Total R$: ${String.format("%.2f", 10 * report.cashTenTokensSold.toFloat())}", 30, 0, false, 0, 80, 0)
                printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
                printSpace(1)
                printHelper.printData("Total Dinheiro - R$: ${String.format("%.2f", report.paymentCash)}", 30, 0, false, 0, 80, 0)
                printHelper.printData("Total Pix      - R$: ${String.format("%.2f", report.paymentPix)}", 30, 0, false, 0, 80, 0)
                printHelper.printData("Total Débito   - R$: ${String.format("%.2f", report.paymentDebit)}", 30, 0, false, 0, 80, 0)
                printHelper.printData("Total Crédito  - R$: ${String.format("%.2f", report.paymentCredit)}", 30, 0, false, 0, 80, 0)
                printHelper.printData("Total Geral    - R$: ${String.format("%.2f", report.paymentCash + report.paymentPix + report.paymentDebit + report.paymentCredit)}", 30, 0, false, 0, 80, 0)
                printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
                printSpace(1)
                printHelper.printData("Sangria:", 30, 0, false, 0, 80, 0)

                sangria.forEach {
                    val date = format.format(it.date)
                    val text = "R$: ${String.format("%.2f", it.sangria)} - $date"
                    printHelper.printData(text, 30, 0, false, 0, 80, 0)
                }

                printSpace(3)
                printHelper.printStart()
                printHelper.cutPaper(1)

                val finalValue = if (binding.edtFinalCash.text.toString().isEmpty()) 0f else {
                    binding.edtFinalCash.text.toString()
                        .replace("R$", "")
                        .replace(",", ".")
                        .trim()
                        .toFloat()
                }

                viewModel.reportSangria()
                viewModel.closeCashRegister(finalValue, calendar)

                findNavController().navigate(R.id.initialCashFragment)
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