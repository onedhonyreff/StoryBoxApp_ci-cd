package com.fikri.submissionstoryappbpai.repository

import android.content.res.Resources
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.data_model.AllStoryResponseModel
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.other_class.reverseGeocoding
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.IOException

class MapsStoryRepository(
    private val resources: Resources,
    private val pref: DataStorePreferencesInterface,
    private val geocoder: Geocoder,
    private val apiService: ApiService
) {
    suspend fun getMapsStory(): ResultWrapper<AllStoryResponseModel> {
        val token = pref.getDataStoreStringValue(DataStorePreferences.TOKEN_KEY).first()
        val apiRequest = apiService.getAllMapStories("Bearer $token", 400, 1)
        try {
            val response: Response<AllStoryResponseModel> = apiRequest.awaitResponse()
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

    fun getMapMode(): LiveData<String> {
        return pref.getDataStoreStringValue(DataStorePreferences.MAP_MODE_KEY).asLiveData()
    }

    fun getAddressName(latLng: LatLng): String {
        return reverseGeocoding(geocoder, latLng, resources.getString(R.string.location_unknown))
    }
}