package com.stellarbitsapps.androidpdv.ui.adapter

import com.stellarbitsapps.androidpdv.database.entity.Tokens

class TokensListener(
    val clickListener: (token: Tokens) -> Unit,
) {
    fun onClick(token: Tokens) {
        clickListener(token)
    }
}