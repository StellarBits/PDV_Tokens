package com.stellarbitsapps.androidpdv.ui.adapter

import com.stellarbitsapps.androidpdv.database.entity.Tokens

class TokensListener(
    val clickListener: (tokens: Tokens) -> Unit,
) {
    fun onClick(tokens: Tokens) {
        clickListener(tokens)
    }
}