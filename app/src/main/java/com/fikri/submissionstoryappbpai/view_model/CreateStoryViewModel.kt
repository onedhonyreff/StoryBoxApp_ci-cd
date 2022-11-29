package com.fikri.submissionstoryappbpai.view_model

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.CreateStoryRepository
import kotlinx.coroutines.launch
import java.io.File

class CreateStoryViewModel(private val createStoryRepository: CreateStoryRepository) :
    ViewModel() {

    private val _photoBitmap = MutableLiveData<Bitmap>()
    val photoBitmap: LiveData<Bitmap> = _photoBitmap
    private val _isShowLoadingModal = MutableLiveData<Boolean>()
    val isShowLoadingModal: LiveData<Boolean> = _isShowLoadingModal
    private val _isShowRefreshModal = MutableLiveData<Boolean>()
    val isShowRefreshModal: LiveData<Boolean> = _isShowRefreshModal
    private val _isShowResponseModal = MutableLiveData<Boolean>()
    val isShowResponseModal: LiveData<Boolean> = _isShowResponseModal
    private val _isAddButtonEnabled = MutableLiveData(false)
    val isAddButtonEnabled: LiveData<Boolean> = _isAddButtonEnabled

    var photo: File? = null
    var firstAppeared = true
    var responseType = ResponseModal.TYPE_GENERAL
    var responseMessage: String? = null

    fun uploadStory(desc: String) {
        viewModelScope.launch {
            _isShowLoadingModal.value = true
            val result = createStoryRepository.postDataToServer(desc, photo)
            _isShowLoadingModal.value = false
            when (result) {
                is ResultWrapper.Success -> {
                    responseType = ResponseModal.TYPE_SUCCESS
                    responseMessage = result.message
                    _isShowResponseModal.value = true
                }
                is ResultWrapper.Error -> {
                    responseType = result.failedType.toString()
                    responseMessage = result.message
                    _isShowRefreshModal.value = true
                }
                is ResultWrapper.NetworkError -> {
                    responseType = result.failedType.toString()
                    responseMessage = result.message
                    _isShowRefreshModal.value = true
                }
            }
        }
    }

    fun setPhotoBitmap(value: Bitmap) {
        _photoBitmap.value = value
    }

    fun setAddButtonEnable(value: Boolean) {
        _isAddButtonEnabled.value = value
    }

    fun dismissRefreshModal() {
        _isShowRefreshModal.value = false
    }

    fun dismissResponseModal() {
        _isShowResponseModal.value = false
    }
}