package com.fikri.submissionstoryappbpai.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface

class MoreMenuRepository(private val pref: DataStorePreferencesInterface) {
    fun getUserName(): LiveData<String> {
        return pref.getDataStoreStringValue(DataStorePreferences.NAME_KEY).asLiveData()
    }

    fun getActualLastLogin(): LiveData<String> {
        return pref.getDataStoreStringValue(DataStorePreferences.LAST_LOGIN_KEY).asLiveData()
    }
}