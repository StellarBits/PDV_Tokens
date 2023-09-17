package com.stellarbitsapps.androidpdv.ui.registertokens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.databinding.FragmentRegisterTokenBinding
import com.stellarbitsapps.androidpdv.ui.initialcash.InitialCashFragmentDirections
import com.stellarbitsapps.androidpdv.ui.tokens.TokensViewModel
import com.stellarbitsapps.androidpdv.ui.tokens.TokensViewModelFactory

class RegisterTokenFragment : Fragment() {
    companion object {
        fun newInstance() = RegisterTokenFragment()
    }

    private val viewModel: RegisterTokenViewModel by activityViewModels {
        RegisterTokenViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.tokensDao()
        )
    }

    private val binding: FragmentRegisterTokenBinding by lazy {
        FragmentRegisterTokenBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val cashEditTextList = listOf<EditText>(
            binding.edtCashOne,
            binding.edtCashTwo,
            binding.edtCashFour,
            binding.edtCashFive,
            binding.edtCashSix,
            binding.edtCashEight,
            binding.edtCashTen
        )

        cashEditTextList.forEach { cashEditText ->
            cashEditText.setOnClickListener {
                incCashQty(cashEditText)
            }

            cashEditText.setOnLongClickListener {
                cashEditText.setText("0")
                true
            }
        }

        binding.btClose.setOnClickListener {
            Navigation.findNavController(requireActivity(), this.id).popBackStack()
        }

        binding.btClear.setOnClickListener {
            clearAllFields(cashEditTextList)
        }

        binding.btRegisterToken.setOnClickListener {
            val token = Tokens(
                value = binding.edtCashValue.text.toString().toFloat(),
                cashOne = cashEditTextList[0].text.toString().toInt(),
                cashTwo = cashEditTextList[1].text.toString().toInt(),
                cashFour = cashEditTextList[2].text.toString().toInt(),
                cashFive = cashEditTextList[3].text.toString().toInt(),
                cashSix = cashEditTextList[4].text.toString().toInt(),
                cashEight = cashEditTextList[5].text.toString().toInt(),
                cashTen = cashEditTextList[6].text.toString().toInt()
            )

            viewModel.setToken(token)

            clearAllFields(cashEditTextList)
        }

        return binding.root
    }

    private fun clearAllFields(cashEditTextList: List<EditText>) {
        cashEditTextList.forEach {
            it.setText("0")
        }
        binding.edtCashValue.setText("")
    }

    @SuppressLint("SetTextI18n")
    private fun incCashQty(textView: TextView) {
        val n = textView.text.toString()
        textView.text = (n.toInt() + 1).toString()
    }
}