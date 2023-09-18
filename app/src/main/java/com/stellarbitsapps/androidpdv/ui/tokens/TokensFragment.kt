package com.stellarbitsapps.androidpdv.ui.tokens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.databinding.FragmentTokensBinding
import com.stellarbitsapps.androidpdv.ui.adapter.TokensAdapter
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recyclerView = binding.rvCashButtons

        val tokensAdapter = TokensAdapter { item ->
            Toast.makeText(requireContext(), "Clicou em $item", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = tokensAdapter
        lifecycle.coroutineScope.launch {
            viewModel.getTokens().collect {
                tokensAdapter.submitList(it)
            }
        }

//        printHelper = AP80PrintHelper.getInstance()
//        printHelper.initPrint(requireContext())
//
//        binding.button.setOnClickListener {
//            Log.i("JAO", "Click!")
//
//            printSpace(1)
//            printSpace(1)
//            printHelper.printQRCode("https://www.gertec.com.br", 2, 1)
//            printSpace(1)
//            printHelper.printBarCode("7899970400070", 2, 100, 200, 1, 0)
//            printHelper.printData("7899970400070", 25, 0, false, 1, 80, 0)
//            printHelper.printData("Example", 100, 0, false, 1, 80, 0)
//            printSpace(7)
//            printHelper.printStart()
//            printHelper.cutPaper(1)
//        }

        return binding.root
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