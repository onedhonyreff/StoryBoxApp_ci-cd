package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerRepository: RegisterRepository) : ViewModel() {
    private val _isShowLoading = MutableLiveData<Boolean>()
    val isShowLoading: LiveData<Boolean> = _isShowLoading
    private val _isShowResponseModal = MutableLiveData<Boolean>()
    val isShowResponseModal: LiveData<Boolean> = _isShowResponseModal

    var firstAppeared = true
    var responseType = ResponseModal.TYPE_GENERAL
    var responseMessage: String? = null

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isShowLoading.value = true
            val result = registerRepository.register(name, email, password)
            _isShowLoading.value = false
            when (result) {
                is ResultWrapper.Success -> {
                    responseType = ResponseModal.TYPE_SUCCESS
                    responseMessage = result.message
                }
                is ResultWrapper.Error -> {
                    responseType = result.failedType.toString()
                    responseMessage = result.message
                }
                is ResultWrapper.NetworkError -> {
                    responseType = result.failedType.toString()
                    responseMessage = result.message
                }
            }
            _isShowResponseModal.value = true
        }
    }

    fun dismissResponseModal() {
        _isShowResponseModal.value = false
    }
}