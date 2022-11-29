package com.fikri.submissionstoryappbpai.repository

import android.content.res.Resources
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data_model.RegisterResponseModel
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import org.json.JSONObject
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.IOException

@Suppress("BlockingMethodInNonBlockingContext")
class RegisterRepository(private val resources: Resources, private val apiService: ApiService) {
    suspend fun register(
        name: String,
        email: String,
        password: String,
    ): ResultWrapper<RegisterResponseModel> {
        val apiRequest = apiService.register(name, email, password)

        try {
            val response: Response<RegisterResponseModel> = apiRequest.awaitResponse()
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
}