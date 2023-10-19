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
import com.serenegiant.utils.UIThreadHelper
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.databinding.FragmentFinalCashBinding
import com.stellarbitsapps.androidpdv.ui.custom.dialog.ProgressHUD
import com.stellarbitsapps.androidpdv.util.PrintUtils
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

    private lateinit var progressHUD: ProgressHUD

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
        viewModel.getReport()
        viewModel.report.observe(viewLifecycleOwner) {
            object : Thread() {
                @SuppressLint("SimpleDateFormat")
                override fun run() {
                    try {
                        val now = Calendar.getInstance().timeInMillis
                        val finalDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(now)
                        val finalValue =
                            if (binding.edtFinalCash.text.toString().isEmpty()) 0f else {
                                binding.edtFinalCash.text.toString()
                                    .replace("R$", "")
                                    .replace(".", "")
                                    .replace(",", ".")
                                    .trim()
                                    .toFloat()
                            }

                        PrintUtils.printReport(
                            it.report,
                            it.sangria,
                            it.error,
                            finalDate,
                            finalValue,
                            printHelper
                        )

                        // Update UI
                        UIThreadHelper.runOnUiThread {
                            viewModel.reportSangria()
                            viewModel.reportErrors()
                            viewModel.closeCashRegister(now, finalValue)

                            findNavController().navigate(R.id.initialCashFragment)

                            progressHUD.dismiss()
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }.start()

            progressHUD = ProgressHUD.show(
                context, "Imprimindo relatório",
                cancelable = false,
                spinnerGone = false
            )
            progressHUD.show()
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