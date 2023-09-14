package com.stellarbitsapps.androidpdv.ui.finalcash

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stellarbitsapps.androidpdv.R

class FinalCashFragment : Fragment() {

    companion object {
        fun newInstance() = FinalCashFragment()
    }

    private lateinit var viewModel: FinalCashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_final_cash, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FinalCashViewModel::class.java)
        // TODO: Use the ViewModel
    }

}