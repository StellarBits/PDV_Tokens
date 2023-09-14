package com.stellarbitsapps.androidpdv.ui.tokens

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stellarbitsapps.androidpdv.R

class TokensFragment : Fragment() {

    companion object {
        fun newInstance() = TokensFragment()
    }

    private lateinit var viewModel: TokensViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tokens, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TokensViewModel::class.java)
        // TODO: Use the ViewModel
    }

}