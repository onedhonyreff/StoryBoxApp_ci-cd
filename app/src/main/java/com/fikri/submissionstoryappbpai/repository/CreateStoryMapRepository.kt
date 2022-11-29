package com.fikri.submissionstoryappbpai.repository

import android.content.res.Resources
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.data_model.AddStoryResponseModel
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.other_class.reduceFileImage
import com.fikri.submissionstoryappbpai.other_class.reverseGeocoding
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.File
import java.io.IOException

@Suppress("BlockingMethodInNonBlockingContext")
class CreateStoryMapRepository(
    private val resources: Resources,
    private val pref: DataStorePreferencesInterface,
    private val geocoder: Geocoder,
    private val apiService: ApiService,
) {
    suspend fun postDataToServer(
        desc: String,
        latLng: LatLng,
        photo: File?,
        isTest: Boolean = false
    ): ResultWrapper<AddStoryResponseModel> {
        val token = pref.getDataStoreStringValue(DataStorePreferences.TOKEN_KEY).first()
        val photoFile = if (!isTest) reduceFileImage(photo as File) else photo as File
        val description = desc.trim().toRequestBody("text/plain".toMediaType())
        val latitude = latLng.latitude.toString().toRequestBody("text/plain".toMediaType())
        val longitude = latLng.longitude.toString().toRequestBody("text/plain".toMediaType())
        val requestImageFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            photoFile.name,
            requestImageFile
        )
        val apiRequest = apiService.addGeolocationStory(
            "Bearer $token",
            imageMultipart,
            description,
            latitude,
            longitude
        )
        try {
            val response: Response<AddStoryResponseModel> = apiRequest.awaitResponse()
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