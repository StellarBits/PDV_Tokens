package com.stellarbitsapps.androidpdv.ui.finalcash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stellarbitsapps.androidpdv.database.dao.ReportDao
import com.stellarbitsapps.androidpdv.database.dao.SangriaDao
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.Sangria
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class FinalCashViewModel(private val reportDao: ReportDao, private val sangriaDao: SangriaDao) : ViewModel() {

    val report = MutableLiveData<Report>()

    val sangria = MutableLiveData<List<Sangria>>()

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

    fun getSangria() {
        viewModelScope.launch {
            sangriaDao.getAll().collect {
                sangria.postValue(it)
            }
        }
    }

    fun reportSangria() {
        viewModelScope.launch {
            sangriaDao.reportSangria()
        }
    }
}

class FinalCashViewModelFactory(
    private val reportDao: ReportDao,
    private val sangriaDao: SangriaDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinalCashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinalCashViewModel(reportDao, sangriaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}