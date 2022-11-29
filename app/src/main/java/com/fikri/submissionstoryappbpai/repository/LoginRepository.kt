package com.fikri.submissionstoryappbpai.repository

import android.content.res.Resources
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.data_model.LoginResponseModel
import com.fikri.submissionstoryappbpai.data_model.LoginResult
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.other_class.getStringDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.IOException

@Suppress("BlockingMethodInNonBlockingContext")
class LoginRepository(
    private val resources: Resources,
    private val pref: DataStorePreferencesInterface,
    private val apiService: ApiService
) {
    suspend fun login(
        email: String,
        password: String
    ): ResultWrapper<LoginResponseModel> {
        val apiRequest = apiService.login(email, password)

        try {
            val response: Response<LoginResponseModel> = apiRequest.awaitResponse()
            if (response.isSuccessful) {
                val responseBody = response.body()
                return if (responseBody != null && !responseBody.error) {
                    ResultWrapper.Success(responseBody, responseBody.message)
                } else {
                    ResultWrapper.Error(
                        response.code(),
                        ResponseModal.TYPE_FAILED,
                        responseBody?.message
                    )
                }
            } else {
                var errorMessage: String? = null
                try {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    errorMessage = jObjError.getString("message")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return ResultWrapper.Error(
                    response.code(),
                    ResponseModal.TYPE_MISTAKE,
                    "${response.message()} | $errorMessage"
                )
            }
        } catch (e: IOException) {
            return ResultWrapper.NetworkError(
                ResponseModal.TYPE_ERROR,
                resources.getString(R.string.connection_problem)
            )
        }
    }

    suspend fun saveLoginData(user: LoginResult?) {
        val currentTime = getStringDate()
        withContext(Dispatchers.Main) {
            pref.saveDataStoreValue(DataStorePreferences.USER_ID_KEY, user?.userId)
            pref.saveDataStoreValue(DataStorePreferences.NAME_KEY, user?.name)
            pref.saveDataStoreValue(DataStorePreferences.TOKEN_KEY, user?.token)
            pref.saveDataStoreValue(DataStorePreferences.SESSION_KEY, currentTime)
            pref.saveDataStoreValue(DataStorePreferences.LAST_LOGIN_KEY, currentTime)
        }
    }
}