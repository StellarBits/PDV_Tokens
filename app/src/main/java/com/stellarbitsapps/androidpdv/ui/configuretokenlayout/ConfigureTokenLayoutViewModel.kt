package com.stellarbitsapps.androidpdv.ui.configuretokenlayout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stellarbitsapps.androidpdv.database.dao.LayoutSettingsDao
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ConfigureTokenLayoutViewModel(private val layoutSettingsDao: LayoutSettingsDao) : ViewModel() {
    fun createConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            layoutSettingsDao.insertAll(LayoutSettings())
        }
    }

    fun getConfigs(): Flow<LayoutSettings> = layoutSettingsDao.getAll()

    fun getRowsCount(): Flow<Int> = layoutSettingsDao.getRowsCount()

    fun updateConfigs(layoutSettings: LayoutSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            layoutSettingsDao.updateConfigs(
                layoutSettings.header,
                layoutSettings.footer,
                layoutSettings.image
            )
        }
    }
}

class ConfigureTokenLayoutViewModelFactory(
    private val layoutSettingsDao: LayoutSettingsDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfigureTokenLayoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConfigureTokenLayoutViewModel(layoutSettingsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}