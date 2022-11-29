package com.fikri.submissionstoryappbpai.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.repository.HomeBottomNavRepository
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
class HomeBottomNavViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var homeBottomNavRepository: HomeBottomNavRepository
    private lateinit var homeBottomNavViewModel: HomeBottomNavViewModel

    @Before
    fun setup() {
        homeBottomNavViewModel = HomeBottomNavViewModel(homeBottomNavRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Changing the Header Icon Will Change the Value of the headerIcon`() = runTest {
        val expectedIcon = R.drawable.ic_dashboard
        homeBottomNavViewModel.changeHeaderIcon(expectedIcon)

        val actualIcon = homeBottomNavViewModel.headerIcon.getOrAwaitValue()

        Assert.assertEquals(expectedIcon, actualIcon)
    }

    @Test
    fun `When Clearing Session Data Will Call Clear DataStore on Repository`() = runTest {
        val expectedNumberOfCall = 1
        homeBottomNavViewModel.clearDataStore()

        Mockito.verify(homeBottomNavRepository).clearDataBeforeLogout()
        Mockito.verify(homeBottomNavRepository, times(expectedNumberOfCall)).clearDataBeforeLogout()
    }
}