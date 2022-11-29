package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.repository.DisplayConfigurationRepository
import kotlinx.coroutines.launch

class DisplayConfigurationViewModel(private val displayConfigurationRepository: DisplayConfigurationRepository) :
    ViewModel() {
    fun getThemeSettings() = displayConfigurationRepository.getThemeSettings()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            displayConfigurationRepository.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getMapMode() = displayConfigurationRepository.getMapMode()

    fun saveMapMode(type: String) {
        viewModelScope.launch {
            displayConfigurationRepository.saveMapMode(type)
        }
    }
}