package com.fikri.submissionstoryappbpai.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.repository.DisplayConfigurationRepository
import com.fikri.submissionstoryappbpai.utils.MainDispatcherRule
import com.fikri.submissionstoryappbpai.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DisplayConfigurationViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var displayConfigurationRepository: DisplayConfigurationRepository
    private lateinit var displayConfigurationViewModel: DisplayConfigurationViewModel

    @Before
    fun setup() {
        displayConfigurationViewModel =
            DisplayConfigurationViewModel(displayConfigurationRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Fetching Theme Settings Will Call Get Theme Setting Function on Repository`() =
        runTest {
            val expectedThemeSetting = MutableLiveData<Boolean>()
            expectedThemeSetting.value = true

            Mockito.`when`(displayConfigurationRepository.getThemeSettings())
                .thenReturn(expectedThemeSetting)

            val actualThemeSettings =
                displayConfigurationViewModel.getThemeSettings().getOrAwaitValue()

            Mockito.verify(displayConfigurationRepository).getThemeSettings()
            Assert.assertEquals(
                expectedThemeSetting.value,
                actualThemeSettings
            )
        }

    @Test
    fun `When Saving the Theme Will Call The saveThemeSetting Function on the Repository`() =
        runTest {
            val expectedNumberOfCall = 1
            val isDarkMode = true

            displayConfigurationViewModel.saveThemeSetting(isDarkMode)

            Mockito.verify(displayConfigurationRepository).saveThemeSetting(isDarkMode)
            Mockito.verify(displayConfigurationRepository, times(expectedNumberOfCall))
                .saveThemeSetting(isDarkMode)
        }

    @Test
    fun `When Fetching Map Settings Will Call getMapMode Function on Repository`() =
        runTest {
            val expectedMapMode = MutableLiveData<String>()
            expectedMapMode.value = DataStorePreferences.MODE_HYBRID

            Mockito.`when`(displayConfigurationRepository.getMapMode())
                .thenReturn(expectedMapMode)

            val actualMapMode =
                displayConfigurationViewModel.getMapMode().getOrAwaitValue()

            Mockito.verify(displayConfigurationRepository).getMapMode()
            Assert.assertEquals(
                expectedMapMode.value,
                actualMapMode
            )
        }

    @Test
    fun `When Saving the Map Mode Will Call the safeModeFunction on the Repository`() = runTest {
        val expectedNumberOfCall = 1
        val mapMode = DataStorePreferences.MODE_HYBRID

        displayConfigurationViewModel.saveMapMode(mapMode)

        Mockito.verify(displayConfigurationRepository).saveMapMode(mapMode)
        Mockito.verify(displayConfigurationRepository, times(expectedNumberOfCall))
            .saveMapMode(mapMode)
    }
}