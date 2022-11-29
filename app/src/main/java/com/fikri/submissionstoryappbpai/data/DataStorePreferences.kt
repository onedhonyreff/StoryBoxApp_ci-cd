package com.fikri.submissionstoryappbpai.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorePreferences private constructor(private val dataStore: DataStore<Preferences>) :
    DataStorePreferencesInterface {
    companion object {
        val SESSION_KEY = stringPreferencesKey("login_session")
        val LAST_LOGIN_KEY = stringPreferencesKey("last_login_session")
        val TOKEN_KEY = stringPreferencesKey("token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val NAME_KEY = stringPreferencesKey("name")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val MAP_MODE_KEY = stringPreferencesKey("map_mode")
        val PAGING_SUCCESS_CODE_KEY = stringPreferencesKey("paging_success_CODE")

        const val MODE_HYBRID = "mode_hybrid"
        const val MODE_NIGHT = "mode_night"
        const val MODE_NORMAL = "mode_normal"

        @Volatile
        private var INSTANCE: DataStorePreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): DataStorePreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = DataStorePreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getDataStoreStringValue(keyParamsString: Preferences.Key<String>): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[keyParamsString]
                ?: when (keyParamsString) {
                    SESSION_KEY, LAST_LOGIN_KEY -> {
                        "0001-01-01 00:00:00.00"
                    }
                    MAP_MODE_KEY -> {
                        MODE_HYBRID
                    }
                    else -> {
                        ""
                    }
                }
        }
    }

    override fun getDataStoreBooleanValue(keyParamsBoolean: Preferences.Key<Boolean>): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[keyParamsBoolean] ?: false
        }
    }

    override suspend fun saveDataStoreValue(
        keyParamsString: Preferences.Key<String>,
        value: String?
    ) {
        dataStore.edit { preferences ->
            preferences[keyParamsString] = value ?: ""
        }
    }

    override suspend fun saveDataStoreValue(
        keyParamsBoolean: Preferences.Key<Boolean>,
        value: Boolean?
    ) {
        dataStore.edit { preferences ->
            preferences[keyParamsBoolean] = value ?: false
        }
    }

    override suspend fun clearDataStore() {
        dataStore.edit { preferences ->
            preferences.remove(SESSION_KEY)
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(NAME_KEY)
            preferences.remove(LAST_LOGIN_KEY)
        }
    }

//    fun getDataStoreStringValue(keyParamsString: Preferences.Key<String>): Flow<String> {
//        return dataStore.data.map { preferences ->
//            preferences[keyParamsString]
//                ?: when (keyParamsString) {
//                    SESSION_KEY, LAST_LOGIN_KEY -> {
//                        "0001-01-01 00:00:00.00"
//                    }
//                    MAP_MODE_KEY -> {
//                        MODE_HYBRID
//                    }
//                    else -> {
//                        ""
//                    }
//                }
//        }
//    }
//
//    fun getDataStoreBooleanValue(keyParamsBoolean: Preferences.Key<Boolean>): Flow<Boolean> {
//        return dataStore.data.map { preferences ->
//            preferences[keyParamsBoolean] ?: false
//        }
//    }
//
//    suspend fun saveDataStoreValue(keyParamsString: Preferences.Key<String>, value: String?) {
//        dataStore.edit { preferences ->
//            preferences[keyParamsString] = value ?: ""
//        }
//    }
//
//    suspend fun saveDataStoreValue(keyParamsBoolean: Preferences.Key<Boolean>, value: Boolean?) {
//        dataStore.edit { preferences ->
//            preferences[keyParamsBoolean] = value ?: false
//        }
//    }
//
//    suspend fun clearDataStore() {
//        dataStore.edit { preferences ->
//            preferences.remove(SESSION_KEY)
//            preferences.remove(TOKEN_KEY)
//            preferences.remove(USER_ID_KEY)
//            preferences.remove(NAME_KEY)
//            preferences.remove(LAST_LOGIN_KEY)
//        }
//    }
}