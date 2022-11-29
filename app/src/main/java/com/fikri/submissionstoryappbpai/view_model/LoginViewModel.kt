package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.data_model.LoginResponseModel
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) :
    ViewModel() {

    private val _isShowLoading = MutableLiveData<Boolean>()
    val isShowLoading: LiveData<Boolean> = _isShowLoading
    private val _isShowResponseModal = MutableLiveData<Boolean>()
    val isShowResponseModal: LiveData<Boolean> = _isShowResponseModal
    private val _isTimeToHome = MutableLiveData<Boolean>()
    val isTimeToHome: LiveData<Boolean> = _isTimeToHome

    var firstAppeared = true
    var responseType = ResponseModal.TYPE_GENERAL
    var responseMessage: String? = null
    var loginResponse: LoginResponseModel? = null

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isShowLoading.value = true
            val result = loginRepository.login(email, password)
            _isShowLoading.value = false
            when (result) {
                is ResultWrapper.Success -> {
                    loginResponse = result.response
                    saveLoginData()
                }
                is ResultWrapper.Error -> {
                    responseType = result.failedType.toString()
                    responseMessage = result.message
                    _isShowResponseModal.value = true
                }
                is ResultWrapper.NetworkError -> {
                    responseType = result.failedType.toString()
                    responseMessage = result.message
                    _isShowResponseModal.value = true
                }
            }
        }
    }

    private fun saveLoginData() {
        viewModelScope.launch {
            loginRepository.saveLoginData(loginResponse?.loginResult)
            _isTimeToHome.value = true
        }
    }

    fun dismissResponseModal() {
        _isShowResponseModal.value = false
    }
}