package com.stellarbitsapps.androidpdv.ui.startscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stellarbitsapps.androidpdv.database.dao.ReportDao
import kotlinx.coroutines.flow.Flow

class StartScreenViewModel(private val reportDao: ReportDao) : ViewModel() {
    fun cashRegisterIsOpen(): Flow<Int> = reportDao.cashRegisterIsOpen()
}

class StartScreenViewModelFactory(
    private val reportDao: ReportDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StartScreenViewModel(reportDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}