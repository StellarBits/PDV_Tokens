package com.stellarbitsapps.androidpdv.ui.initialcash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.stellarbitsapps.androidpdv.R
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
        setHasOptionsMenu(true)

        binding.button.setOnClickListener {
            val direction = InitialCashFragmentDirections.actionInitialCashFragmentToTokensFragment()
            findNavController().navigate(direction)
        }

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