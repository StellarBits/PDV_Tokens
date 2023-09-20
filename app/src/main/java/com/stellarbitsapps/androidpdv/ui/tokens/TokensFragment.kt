package com.stellarbitsapps.androidpdv.ui.tokens

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.databinding.FragmentTokensBinding
import com.stellarbitsapps.androidpdv.ui.adapter.TokensAdapter
import com.stellarbitsapps.androidpdv.ui.adapter.TokensListener
import com.stellarbitsapps.androidpdv.util.Utils
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

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
            selectedTokensList.clear()
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

    @SuppressLint("SimpleDateFormat")
    private fun printToken(tokenValue: String) {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val date = format.format(calendar.time)

        val imgFile = File(Environment.getExternalStorageDirectory().absolutePath + "/PDV/img_small.jpg")
        val myBitmap = BitmapFactory.decodeFile(imgFile.toString())

        val tokenLayout = layoutInflater.inflate(R.layout.token_layout, null)

        tokenLayout.findViewById<TextView>(R.id.tv_token_value).text = tokenValue
        tokenLayout.findViewById<ImageView>(R.id.img_token_image).setImageBitmap(myBitmap)

        //tokenImage = Environment.getExternalStorageDirectory().absolutePath + "/PDV/img_small.jpg"

        val bitmap = Utils.createBitmapFromConstraintLayout(tokenLayout)

        printHelper.printData("FESTA DE SÃO JUDAS TADEU 2023", 35, 0, false, 1, 80, 0)
        printHelper.printData("VALE R$ 2,00", 80, 0, false, 1, 80, 0)
        printHelper.printBitmap(bitmap, 2, 80)
        printHelper.printData(date, 30, 0, false, 0, 80, 0)
        printHelper.printData("AGRADECEMOS SUA PRESENÇA!", 40, 0, false, 0, 80, 0)
        printSpace(3)
        printHelper.printStart()
        printHelper.cutPaper(1)
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