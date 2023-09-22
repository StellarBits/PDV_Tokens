package com.stellarbitsapps.androidpdv.ui.startscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.database.dao.ReportDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StartScreenViewModel(private val reportDao: ReportDao) : ViewModel() {
    val cashRegisterIsOpen = MutableLiveData(false)

    //fun cashRegisterIsOpen(): Flow<Int> = reportDao.cashRegisterIsOpen()

    fun cashRegisterIsOpen() {
        viewModelScope.launch {
            reportDao.cashRegisterIsOpen().collect {
                cashRegisterIsOpen.postValue(it != 0)
            }
        }
    }
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