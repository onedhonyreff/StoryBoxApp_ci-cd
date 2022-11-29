package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.repository.MainActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivityViewModel(private val mainActivityRepository: MainActivityRepository) :
    ViewModel() {
    private val _isTimeOut = MutableLiveData(false)
    val isTimeOut: LiveData<Boolean> = _isTimeOut
    private val _isValidSession = MutableLiveData<Boolean>()
    val isValidSession: LiveData<Boolean> = _isValidSession

    init {
        viewModelScope.launch {
            waitAMoment()
        }
    }

    fun getThemeSettings() = mainActivityRepository.getThemeSettings()

    suspend fun waitAMoment() {
        delay(2500)
        withContext(Dispatchers.Main) {
            _isTimeOut.value = true
        }
    }

    fun validatingLoginSession(currentDate: Date) {
        viewModelScope.launch {
            _isValidSession.value =
                mainActivityRepository.validatingLoginSession(currentDate)
        }
    }

    fun saveCurrentSession() {
        viewModelScope.launch {
            mainActivityRepository.saveCurrentSession()
        }
    }
}