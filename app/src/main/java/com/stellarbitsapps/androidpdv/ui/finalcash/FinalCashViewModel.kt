package com.stellarbitsapps.androidpdv.ui.finalcash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stellarbitsapps.androidpdv.database.dao.ReportDao
import com.stellarbitsapps.androidpdv.database.entity.Report
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class FinalCashViewModel(private val reportDao: ReportDao) : ViewModel() {

    val report = MutableLiveData<Report>()

    fun getReport() {
        viewModelScope.launch {
            reportDao.getAll().collect {
                report.postValue(it)
            }
        }
    }

    fun closeCashRegister(finalValue: Float, calendar: Calendar) {
        viewModelScope.launch {
            reportDao.updateReportFinalValue(finalValue, calendar.timeInMillis)
        }
    }
}

class FinalCashViewModelFactory(
    private val reportDao: ReportDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinalCashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinalCashViewModel(reportDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}