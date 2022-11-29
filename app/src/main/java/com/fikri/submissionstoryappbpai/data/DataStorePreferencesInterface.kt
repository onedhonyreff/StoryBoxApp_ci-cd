package com.fikri.submissionstoryappbpai.data

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStorePreferencesInterface {

    fun getDataStoreStringValue(keyParamsString: Preferences.Key<String>): Flow<String>

    fun getDataStoreBooleanValue(keyParamsBoolean: Preferences.Key<Boolean>): Flow<Boolean>

    suspend fun saveDataStoreValue(keyParamsString: Preferences.Key<String>, value: String?)

    suspend fun saveDataStoreValue(keyParamsBoolean: Preferences.Key<Boolean>, value: Boolean?)

    suspend fun clearDataStore()
}