package com.fikri.submissionstoryappbpai.data.faker

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeDataStore : DataStorePreferencesInterface {
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
    }

    private var session: String? = null
    private var lastLogin: String? = null
    private var token: String? = null
    private var userId: String? = null
    private var name: String? = null
    private var darkMode: Boolean? = null
    private var mapMode: String? = null
    private var pagingSuccessCode: String? = null

    override fun getDataStoreStringValue(keyParamsString: Preferences.Key<String>): Flow<String> {
        return flowOf(
            when (keyParamsString) {
                SESSION_KEY -> session ?: "0001-01-01 00:00:00.00"
                LAST_LOGIN_KEY -> lastLogin ?: "0001-01-01 00:00:00.00"
                TOKEN_KEY -> token ?: ""
                USER_ID_KEY -> userId ?: ""
                NAME_KEY -> name ?: ""
                MAP_MODE_KEY -> mapMode ?: MODE_HYBRID
                PAGING_SUCCESS_CODE_KEY -> pagingSuccessCode ?: ""
                else -> ""
            }
        )
    }

    override fun getDataStoreBooleanValue(keyParamsBoolean: Preferences.Key<Boolean>): Flow<Boolean> {
        return flowOf(
            when (keyParamsBoolean) {
                DARK_MODE_KEY -> darkMode ?: false
                else -> false
            }
        )
    }

    override suspend fun saveDataStoreValue(
        keyParamsString: Preferences.Key<String>,
        value: String?
    ) {
        when (keyParamsString) {
            SESSION_KEY -> session = value
            LAST_LOGIN_KEY -> lastLogin = value
            TOKEN_KEY -> token = value
            USER_ID_KEY -> userId = value
            NAME_KEY -> name = value
            MAP_MODE_KEY -> mapMode = value
            PAGING_SUCCESS_CODE_KEY -> pagingSuccessCode = value
        }
    }

    override suspend fun saveDataStoreValue(
        keyParamsBoolean: Preferences.Key<Boolean>,
        value: Boolean?
    ) {
        when (keyParamsBoolean) {
            DARK_MODE_KEY -> darkMode = value
        }
    }

    override suspend fun clearDataStore() {
        clearData()
    }

    private fun clearData() {
        session = null
        lastLogin = null
        token = null
        userId = null
        name = null
        darkMode = null
        mapMode = null
        pagingSuccessCode = null
    }
}