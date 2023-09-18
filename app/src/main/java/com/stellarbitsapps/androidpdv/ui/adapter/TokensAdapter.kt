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
    private val tokensListener: (tokens: Tokens) -> Unit
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

    class TokensViewHolder(binding: TokensItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val button: Button = binding.btToken

        @SuppressLint("SetTextI18n")
        fun bind(item: Tokens) {
            button.text = "R$ " + String.format("%.2f", item.value)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokensViewHolder {
        val viewHolder = TokensViewHolder(
            TokensItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            tokensListener(getItem(position))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: TokensViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}