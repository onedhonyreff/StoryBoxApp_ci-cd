package com.fikri.submissionstoryappbpai.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.other_class.getDayDiff
import com.fikri.submissionstoryappbpai.other_class.getStringDate
import com.fikri.submissionstoryappbpai.other_class.toDate
import kotlinx.coroutines.flow.first
import java.util.*

class MainActivityRepository(
    private val pref: DataStorePreferencesInterface
) {
    suspend fun validatingLoginSession(currentDateTime: Date): Boolean {
        val lastLoginDate =
            pref.getDataStoreStringValue(DataStorePreferences.SESSION_KEY).first().toDate()
        val token = pref.getDataStoreStringValue(DataStorePreferences.TOKEN_KEY).first()
        return getDayDiff(lastLoginDate, currentDateTime) < 3 && token.isNotEmpty()
    }

    suspend fun saveCurrentSession() {
        pref.saveDataStoreValue(DataStorePreferences.SESSION_KEY, getStringDate())
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getDataStoreBooleanValue(DataStorePreferences.DARK_MODE_KEY).asLiveData()
    }
}