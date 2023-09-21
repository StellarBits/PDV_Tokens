package com.stellarbitsapps.androidpdv.ui.initialcash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stellarbitsapps.androidpdv.database.dao.ReportDao
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InitialCashViewModel(private val reportDao: ReportDao) : ViewModel() {
    fun addReport(report: Report) {
        viewModelScope.launch(Dispatchers.IO) {
            reportDao.insertReport(report)
        }
    }

    fun getLastFinalValue(): Float = reportDao.getLastFinalValue()
}

class InitialCashViewModelFactory(
    private val reportDao: ReportDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InitialCashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InitialCashViewModel(reportDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}