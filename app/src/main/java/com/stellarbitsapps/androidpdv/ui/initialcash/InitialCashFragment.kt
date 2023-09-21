package com.stellarbitsapps.androidpdv.ui.initialcash

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.databinding.FragmentInitialCashBinding
import com.stellarbitsapps.androidpdv.ui.tokens.TokensViewModel
import com.stellarbitsapps.androidpdv.ui.tokens.TokensViewModelFactory
import com.stellarbitsapps.androidpdv.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat


class InitialCashFragment : Fragment() {

    companion object {
        fun newInstance() = InitialCashFragment()
    }

    private val viewModel: InitialCashViewModel by activityViewModels {
        InitialCashViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.reportDao()
        )
    }

    private val binding: FragmentInitialCashBinding by lazy {
        FragmentInitialCashBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        val lastFinalValue = MutableLiveData(0f)

        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            lastFinalValue.postValue(viewModel.getLastFinalValue())
        }

        lastFinalValue.observe(viewLifecycleOwner) {
            if (it == 0f) {
                val direction = InitialCashFragmentDirections.actionInitialCashFragmentToTokensFragment()
                findNavController().navigate(direction)
            }
        }

        binding.button.setOnClickListener {
            val initialCash = binding.edtInitialCash.text.toString()
                .replace("R$", "")
                .replace(",", ".")
                .trim()
                .toFloat()

            viewModel.addReport(Report(initialCash = initialCash))

            val direction = InitialCashFragmentDirections.actionInitialCashFragmentToTokensFragment()
            findNavController().navigate(direction)
        }

        binding.edtInitialCash.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Utils.formatCashTextMask(s, binding.edtInitialCash, this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun afterTextChanged(s: Editable) { }
        })

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.config_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.register_tokens -> {
                val direction = InitialCashFragmentDirections.actionInitialCashFragmentToRegisterTokenFragment()
                findNavController().navigate(direction)
                true
            }

            R.id.configure_tokens_layout -> {
                val direction = InitialCashFragmentDirections.actionInitialCashFragmentToConfigureTokenLayoutFragment()
                findNavController().navigate(direction)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}