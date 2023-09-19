package com.stellarbitsapps.androidpdv.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.databinding.TokensItemBinding

class TokensAdapter(
    private val tokensListener: TokensListener
) : ListAdapter<Tokens, TokensAdapter.TokensViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Tokens>() {
            override fun areItemsTheSame(oldItem: Tokens, newItem: Tokens): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Tokens, newItem: Tokens): Boolean {
                return oldItem == newItem
            }
        }
    }

    class TokensViewHolder(private val binding: TokensItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Tokens, buttonListener: TokensListener) {
            with(binding) {
                btToken.text = "R$ " + String.format("%.2f", item.value)
                token = item
                listener = buttonListener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokensViewHolder {
        return TokensViewHolder(TokensItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TokensViewHolder, position: Int) {
        val item = getItem(position) as Tokens
        holder.bind(item, tokensListener)
    }
}