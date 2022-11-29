package com.fikri.submissionstoryappbpai.data_model

sealed class ResultWrapper<out T> {
    data class Success<out T>(val response: T, val message: String? = null) : ResultWrapper<T>()
    data class Error(
        val code: Int? = null,
        val failedType: String? = null,
        val message: String? = null
    ) : ResultWrapper<Nothing>()

    data class NetworkError(val failedType: String? = null, val message: String? = null) :
        ResultWrapper<Nothing>()
}