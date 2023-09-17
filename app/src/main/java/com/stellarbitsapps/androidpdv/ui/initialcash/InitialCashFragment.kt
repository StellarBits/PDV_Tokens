package com.stellarbitsapps.androidpdv.ui.initialcash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.stellarbitsapps.androidpdv.databinding.FragmentInitialCashBinding

class InitialCashFragment : Fragment() {

    companion object {
        fun newInstance() = InitialCashFragment()
    }

    private lateinit var viewModel: InitialCashViewModel

    private val binding: FragmentInitialCashBinding by lazy {
        FragmentInitialCashBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.button.setOnClickListener {
            val direction = InitialCashFragmentDirections.actionInitialCashFragmentToTokensFragment()
            findNavController().navigate(direction)
        }

        binding.btRegisterToken.setOnClickListener {
            val direction = InitialCashFragmentDirections.actionInitialCashFragmentToRegisterTokenFragment()
            findNavController().navigate(direction)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InitialCashViewModel::class.java)
        // TODO: Use the ViewModel
    }

}