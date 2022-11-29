package com.fikri.submissionstoryappbpai.data.faker

import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateLoginResponse
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateRegisterResponse
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateStoryListResponse
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateStoryMapResponse
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateUploadStoryMapResponse
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateUploadStoryResponse
import com.fikri.submissionstoryappbpai.data_model.AddStoryResponseModel
import com.fikri.submissionstoryappbpai.data_model.AllStoryResponseModel
import com.fikri.submissionstoryappbpai.data_model.LoginResponseModel
import com.fikri.submissionstoryappbpai.data_model.RegisterResponseModel
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FakeApiService : ApiService {
    override fun register(
        name: String,
        email: String,
        password: String
    ): Call<RegisterResponseModel> {
        return FakeCall(
            Response.success(200, generateRegisterResponse())
        )
    }

    override fun login(email: String, password: String): Call<LoginResponseModel> {
        return FakeCall(
            Response.success(200, generateLoginResponse())
        )
    }

    override suspend fun getAllBasicStories(
        authorization: String,
        page: Int,
        size: Int,
        locationEnable: Int
    ): AllStoryResponseModel {
        return generateStoryListResponse(page, size)
    }

    override fun addStory(
        authorization: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): Call<AddStoryResponseModel> {
        return FakeCall(
            Response.success(200, generateUploadStoryResponse())
        )
    }

    override fun getAllMapStories(
        authorization: String,
        size: Int,
        locationEnable: Int
    ): Call<AllStoryResponseModel> {
        return FakeCall(
            Response.success(200, generateStoryMapResponse())
        )
    }

    override fun addGeolocationStory(
        authorization: String,
        file: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody
    ): Call<AddStoryResponseModel> {
        return FakeCall(
            Response.success(200, generateUploadStoryMapResponse())
        )
    }
}

@Suppress("UNCHECKED_CAST")
class FakeCall<T>(private val fakeResponse: Response<T>) : FakeCallAbstract<T>() {
    override fun enqueue(callback: Callback<T>) {
        callback.onResponse(
            this, fakeResponse
        )
    }
}

abstract class FakeCallAbstract<T> : Call<T> {
    override fun clone(): Call<T> {
        TODO("Not yet implemented")
    }

    override fun execute(): Response<T> {
        TODO("Not yet implemented")
    }

    override fun enqueue(callback: Callback<T>) {
        TODO("Not yet implemented")
    }

    override fun isExecuted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun isCanceled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun request(): Request {
        TODO("Not yet implemented")
    }

    override fun timeout(): Timeout {
        TODO("Not yet implemented")
    }
}