package com.stellarbitsapps.androidpdv.ui.initialcash

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.databinding.FragmentInitialCashBinding
import com.stellarbitsapps.androidpdv.util.Utils
import java.text.NumberFormat


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
        setHasOptionsMenu(true)

        binding.button.setOnClickListener {
            val direction = InitialCashFragmentDirections.actionInitialCashFragmentToTokensFragment()
            findNavController().navigate(direction)
        }

        binding.edtInitialCash.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Utils.formatCashTextMask(s, binding.edtInitialCash, this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing
            }
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InitialCashViewModel::class.java)
        // TODO: Use the ViewModel
    }

}