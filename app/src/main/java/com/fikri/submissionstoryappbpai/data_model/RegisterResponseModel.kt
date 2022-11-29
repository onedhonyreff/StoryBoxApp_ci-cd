package com.fikri.submissionstoryappbpai.data_model

import com.google.gson.annotations.SerializedName

data class RegisterResponseModel(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
