package com.stellarbitsapps.androidpdv.ui.startscreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.databinding.FragmentStartScreenBinding
import kotlinx.coroutines.launch

class StartScreenFragment : Fragment() {

    companion object {
        fun newInstance() = StartScreenFragment()
    }

    private val viewModel: StartScreenViewModel by activityViewModels {
        StartScreenViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.reportDao()
        )
    }

    private val binding: FragmentStartScreenBinding by lazy {
        FragmentStartScreenBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.cashRegisterIsOpen()
        viewModel.cashRegisterIsOpen.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.tokensFragment)
            } else {
                findNavController().navigate(R.id.initialCashFragment)
            }
        }

        return binding.root
    }

}