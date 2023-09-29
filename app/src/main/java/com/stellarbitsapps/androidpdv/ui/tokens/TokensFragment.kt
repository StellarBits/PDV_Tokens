package com.stellarbitsapps.androidpdv.ui.tokens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.databinding.FragmentTokensBinding
import com.stellarbitsapps.androidpdv.ui.adapter.TokensAdapter
import com.stellarbitsapps.androidpdv.util.Utils

class TokensFragment : Fragment() {

    companion object {
        fun newInstance() = TokensFragment()
    }

    private val viewModel: TokensViewModel by activityViewModels {
        TokensViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.tokensDao(),
            (requireActivity().application as AndroidPdvApplication).database.reportDao(),
            (requireActivity().application as AndroidPdvApplication).database.layoutSettingsDao(),
            (requireActivity().application as AndroidPdvApplication).database.sangriaDao()
        )
    }

    private val binding: FragmentTokensBinding by lazy {
        FragmentTokensBinding.inflate(layoutInflater)
    }

    private lateinit var tokensAdapter: TokensAdapter

    private lateinit var recyclerView: RecyclerView

    private var tokenSum = 0f

    private var selectedTokensList = arrayListOf<Tokens>()

    private var tokenSettings = LayoutSettings()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initRecyclerView()

        loadTokenLayoutSettings()

        binding.btClean.setOnClickListener {
            clearFields()
        }

        binding.btClose.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Atenção!")
                .setMessage("Você realmente deseja sair para fechar o caixa?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sim") { _, _ ->
                    findNavController().navigate(R.id.finalCashFragment)
                }
                .setNegativeButton("Não", null).show()
        }

        binding.btCash.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val tokenValues = arrayOf(tokenSum, 0f, 0f, 0f)
            Utils.tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, this)

            Utils.showCashDialog(this, viewModel, false, tokenSum)
        }

        binding.btPix.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val tokenValues = arrayOf(0f, tokenSum, 0f, 0f)
            Utils.tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, this)
        }

        binding.btDebit.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val tokenValues = arrayOf(0f, 0f, tokenSum, 0f)
            Utils.tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, this)
        }

        binding.btCredit.setOnClickListener {
            // Cash, Pix, Debit, Credit in this order
            val tokenValues = arrayOf(0f, 0f, 0f, tokenSum)
            Utils.tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, this)
        }

        binding.btSangria.setOnClickListener {
            Utils.showCashDialog(this, viewModel, true, tokenSum)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initRecyclerView() {
        tokensAdapter = TokensAdapter(this)

        recyclerView = binding.rvCashButtons

        recyclerView.adapter = tokensAdapter

        viewModel.getTokens()
        viewModel.tokensList.observe(viewLifecycleOwner) {
            tokensAdapter.submitList(it)
        }
    }

    private fun loadTokenLayoutSettings() {
        viewModel.getConfigs()
        viewModel.layoutSettings.observe(viewLifecycleOwner) {
            tokenSettings = it
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearFields() {
        binding.tvTotaValue.text = "R$ 0,00"
        tokenSum = 0f
        selectedTokensList.clear()

        tokensAdapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    fun tokenClicked(token: Tokens) {
        selectedTokensList.add(token)
        tokenSum += token.value
        binding.tvTotaValue.text = "R$ " + String.format("%.2f", tokenSum)
    }
}