package com.fikri.submissionstoryappbpai.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fikri.submissionstoryappbpai.other_class.getStringDate
import com.fikri.submissionstoryappbpai.other_class.toDate
import com.fikri.submissionstoryappbpai.repository.MainActivityRepository
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainActivityViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mainActivityRepository: MainActivityRepository
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private val currentDate = getStringDate().toDate()

    @Before
    fun setup() {
        mainActivityViewModel = MainActivityViewModel(mainActivityRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Fetching the Dark Theme Configuration According to the Repository Returns`() =
        runTest {
            val expectedDarkMode = MutableLiveData<Boolean>()
            expectedDarkMode.value = true

            `when`(mainActivityRepository.getThemeSettings()).thenReturn(expectedDarkMode)

            val actualDarkMode = mainActivityViewModel.getThemeSettings().getOrAwaitValue()

            Mockito.verify(mainActivityRepository).getThemeSettings()
            Assert.assertEquals(
                expectedDarkMode.value,
                actualDarkMode
            )
        }

    @Test
    fun `The First Time Initialization Will Change the isTimeOut Variable to True`() = runTest {
        val expectedValue = MutableLiveData<Boolean>()
        expectedValue.value = true

        mainActivityViewModel.waitAMoment()
        val actualValue = mainActivityViewModel.isTimeOut.getOrAwaitValue()

        Assert.assertEquals(
            expectedValue.value,
            actualValue
        )
    }

    @Test
    fun `When Checking the Session Will Call the Validation Function and Change the isValidSession Variable`() =
        runTest {
            val expectedValidSession = MutableLiveData<Boolean>()
            expectedValidSession.value = true

            `when`(mainActivityRepository.validatingLoginSession(currentDate)).thenReturn(true)

            mainActivityViewModel.validatingLoginSession(currentDate)
            val actualValidSession = mainActivityViewModel.isValidSession.getOrAwaitValue()

            Mockito.verify(mainActivityRepository).validatingLoginSession(currentDate)
            Assert.assertEquals(
                expectedValidSession.value,
                actualValidSession
            )
        }

    @Test
    fun `When Saving Session Will Call saveCurrentSession Function Once on Repository`() = runTest {
        val expectedNumberOfCall = 1

        mainActivityViewModel.saveCurrentSession()

        Mockito.verify(mainActivityRepository, times(expectedNumberOfCall)).saveCurrentSession()
    }
}