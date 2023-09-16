package com.stellarbitsapps.androidpdv.ui.tokens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.Tokens
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tokens, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = Tokens(
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
        }
    }
}