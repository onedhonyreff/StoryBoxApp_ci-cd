package com.fikri.submissionstoryappbpai.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import kotlinx.coroutines.CoroutineScope

class DisplayConfigurationRepository(
    private val pref: DataStorePreferencesInterface
) {
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getDataStoreBooleanValue(DataStorePreferences.DARK_MODE_KEY).asLiveData()
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        pref.saveDataStoreValue(DataStorePreferences.DARK_MODE_KEY, isDarkModeActive)
    }

    fun getMapMode(): LiveData<String> {
        return pref.getDataStoreStringValue(DataStorePreferences.MAP_MODE_KEY).asLiveData()
    }

    suspend fun saveMapMode(type: String) {
        pref.saveDataStoreValue(DataStorePreferences.MAP_MODE_KEY, type)
    }
}