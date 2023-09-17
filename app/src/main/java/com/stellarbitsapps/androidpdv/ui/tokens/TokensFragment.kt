package com.stellarbitsapps.androidpdv.ui.tokens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.databinding.FragmentTokensBinding

class TokensFragment : Fragment() {

    companion object {
        fun newInstance() = TokensFragment()
    }

    private val viewModel: TokensViewModel by activityViewModels {
        TokensViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.tokensDao()
        )
    }

    private val binding: FragmentTokensBinding by lazy {
        FragmentTokensBinding.inflate(layoutInflater)
    }

    private lateinit var printHelper: AP80PrintHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        printHelper = AP80PrintHelper.getInstance()
        printHelper.initPrint(requireContext())

        binding.button.setOnClickListener {
            Log.i("JAO", "Click!")

            printSpace(1)
            printSpace(1)
            printHelper.printQRCode("https://www.gertec.com.br", 2, 1)
            printSpace(1)
            printHelper.printBarCode("7899970400070", 2, 100, 200, 1, 0)
            printHelper.printData("7899970400070", 25, 0, false, 1, 80, 0)
            printHelper.printData("Example", 100, 0, false, 1, 80, 0)
            printSpace(7)
            printHelper.printStart()
            printHelper.cutPaper(1)
        }

        return binding.root
    }

    private fun printSpace(n: Int) {
        if (n < 0) {
            return
        }
        val str_space = StringBuilder()
        for (i in 0 until n) {
            str_space.append("\n")
        }
        printHelper.printData(str_space.toString(), 32, 0, false, 1, 80, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Room example
        /* val token = Tokens(
            value = 1.0f,
            cashOne = 1,
            cashTwo = 0,
            cashFour = 0,
            cashFive = 0,
            cashSix = 0,
            cashEight = 0,
            cashTen = 0
        )

        viewModel.setToken(token)

        lifecycle.coroutineScope.launch {
            val list = viewModel.getTokens()
            Log.i("JAO", "Tokens: $list")
        } */
    }
}