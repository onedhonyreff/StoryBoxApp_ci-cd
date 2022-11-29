package com.fikri.submissionstoryappbpai.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.utils.MainDispatcherRule
import com.fikri.submissionstoryappbpai.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DisplayConfigurationRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var pref: DataStorePreferencesInterface
    private lateinit var displayConfigurationRepository: DisplayConfigurationRepository

    @Before
    fun setup() {
        displayConfigurationRepository = DisplayConfigurationRepository(pref)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Fetching Theme Settings, The Return Data Will Match the Datastore`() = runTest {
        val expectedThemeSetting = flowOf(true)

        `when`(pref.getDataStoreBooleanValue(DataStorePreferences.DARK_MODE_KEY))
            .thenReturn(expectedThemeSetting)

        val actualThemeSetting =
            displayConfigurationRepository.getThemeSettings().getOrAwaitValue()

        Mockito.verify(pref).getDataStoreBooleanValue(DataStorePreferences.DARK_MODE_KEY)
        Assert.assertEquals(expectedThemeSetting.first(), actualThemeSetting)
    }

    @Test
    fun `When Saving Theme Settings With Arguments DARK_MODE_KEY and True`() = runTest {
        val darkModeKey = DataStorePreferences.DARK_MODE_KEY
        val isDarkMode = true

        displayConfigurationRepository.saveThemeSetting(isDarkMode)

        Mockito.verify(pref).saveDataStoreValue(darkModeKey, isDarkMode)
    }

    @Test
    fun `When Fetching Map Settings, Data Is Not Null and The Return Data Will Match the Datastore`() = runTest {
        val expectedMapSetting = flowOf(DataStorePreferences.MODE_HYBRID)

        `when`(pref.getDataStoreStringValue(DataStorePreferences.MAP_MODE_KEY))
            .thenReturn(expectedMapSetting)

        val actualMapSetting =
            displayConfigurationRepository.getMapMode().getOrAwaitValue()

        Mockito.verify(pref).getDataStoreStringValue(DataStorePreferences.MAP_MODE_KEY)
        Assert.assertNotNull(actualMapSetting)
        Assert.assertEquals(expectedMapSetting.first(), actualMapSetting)
    }

    @Test
    fun `When Saving Map Settings With Arguments MAP_MODE_KEY and NIGHT_MODE`() = runTest {
        val mapModeKey = DataStorePreferences.MAP_MODE_KEY
        val mapMode = DataStorePreferences.MODE_NIGHT

        displayConfigurationRepository.saveMapMode(mapMode)

        Mockito.verify(pref).saveDataStoreValue(mapModeKey, mapMode)
    }
}