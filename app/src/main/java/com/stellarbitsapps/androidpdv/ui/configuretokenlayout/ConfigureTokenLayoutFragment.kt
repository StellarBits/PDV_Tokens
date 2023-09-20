package com.stellarbitsapps.androidpdv.ui.configuretokenlayout

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stellarbitsapps.androidpdv.R

class ConfigureTokenLayoutFragment : Fragment() {

    companion object {
        fun newInstance() = ConfigureTokenLayoutFragment()
    }

    private lateinit var viewModel: ConfigureTokenLayoutViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configure_token_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConfigureTokenLayoutViewModel::class.java)
        // TODO: Use the ViewModel
    }

}