package com.stellarbitsapps.androidpdv.ui.tokens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stellarbitsapps.androidpdv.database.dao.ReportDao
import com.stellarbitsapps.androidpdv.database.dao.TokensDao
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TokensViewModel(private val tokensDao: TokensDao, private val reportDao: ReportDao) : ViewModel() {
    fun getTokens(): Flow<List<Tokens>> = tokensDao.getAll()

    fun setReport(report: Report) {
        viewModelScope.launch(Dispatchers.IO) {
            reportDao.insertAll(report)
        }
    }
}

class TokensViewModelFactory(
    private val tokensDao: TokensDao,
    private val reportDao: ReportDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TokensViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TokensViewModel(tokensDao, reportDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}