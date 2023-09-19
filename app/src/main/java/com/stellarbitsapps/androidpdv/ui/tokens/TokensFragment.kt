package com.stellarbitsapps.androidpdv.ui.tokens

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.databinding.FragmentTokensBinding
import com.stellarbitsapps.androidpdv.ui.adapter.TokensAdapter
import com.stellarbitsapps.androidpdv.ui.adapter.TokensListener
import kotlinx.coroutines.launch

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

    private lateinit var recyclerView: RecyclerView

    private lateinit var printHelper: AP80PrintHelper

    private var tokenSum = 0f

    private var selectedTokensList = arrayListOf<Tokens>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        printHelper = AP80PrintHelper.getInstance()
        printHelper.initPrint(requireContext())

        initRecyclerView()

        binding.btClean.setOnClickListener {
            binding.tvTotaValue.text = "R$ 0,00"
            tokenSum = 0f
        }

        binding.btExit.setOnClickListener {
            val direction = TokensFragmentDirections.actionTokensFragmentToFinalCashFragment()
            findNavController().navigate(direction)
        }

        binding.btCash.setOnClickListener {

            Log.i("JAO", "Tokens List: $selectedTokensList")

            selectedTokensList.forEach { token ->

                val auxTokensList = listOf(
                    Pair(token.cashOne, "R$ 1,00"),
                    Pair(token.cashTwo, "R$ 2,00"),
                    Pair(token.cashFour, "R$ 4,00"),
                    Pair(token.cashFive, "R$ 5,00"),
                    Pair(token.cashSix, "R$ 6,00"),
                    Pair(token.cashEight, "R$ 8,00"),
                    Pair(token.cashTen, "R$ 10,00")
                )

                auxTokensList.forEach { tokensPair ->
                    if (tokensPair.first > 0) {
                        for (i in 1..tokensPair.first) {
                            printToken(tokensPair.second)
                        }
                    }
                }
            }
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

    private fun printToken(formOfPayment: String) {
        printSpace(3)
        printHelper.printData(formOfPayment, 100, 0, false, 1, 80, 0)
        printSpace(3)
        printHelper.printStart()
        printHelper.cutPaper(1)

        Log.i("JAO", "Click!")

//        printHelper.printData(formOfPayment, 100, 0, false, 1, 80, 0)
//        printSpace(1)
//        printHelper.printQRCode("https://www.gertec.com.br", 2, 1)
//        printSpace(1)
//        printHelper.printData("Total", 100, 0, false, 1, 80, 0)
//        printSpace(7)
//        printHelper.printStart()
//        printHelper.cutPaper(1)
    }

    private fun printSpace(n: Int) {
        if (n < 0) {
            return
        }
        val strSpace = StringBuilder()
        for (i in 0 until n) {
            strSpace.append("\n")
        }
        printHelper.printData(strSpace.toString(), 32, 0, false, 1, 80, 0)
    }
}