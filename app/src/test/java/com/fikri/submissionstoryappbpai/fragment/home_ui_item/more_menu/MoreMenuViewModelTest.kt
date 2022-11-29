package com.fikri.submissionstoryappbpai.fragment.home_ui_item.more_menu

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fikri.submissionstoryappbpai.other_class.getStringDate
import com.fikri.submissionstoryappbpai.repository.MoreMenuRepository
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MoreMenuViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var moreMenuRepository: MoreMenuRepository
    private lateinit var moreMenuViewModel: MoreMenuViewModel

    @Before
    fun setup() {
        moreMenuViewModel = MoreMenuViewModel(moreMenuRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Fetching Username Will Call getUsername Function on Repository and Not Null`() =
        runTest {
            val expectedUserName = MutableLiveData<String>()
            expectedUserName.value = "Indry Puji Lestari"

            `when`(moreMenuRepository.getUserName()).thenReturn(expectedUserName)

            val actualUserName = moreMenuViewModel.getUserName().getOrAwaitValue()

            verify(moreMenuRepository).getUserName()
            Assert.assertNotNull(actualUserName)
            Assert.assertEquals(expectedUserName.value, actualUserName)
        }

    @Test
    fun `When Fetching lastLogin It Will Call getActualLastLogin Function on Repository and Not Null`() =
        runTest {
            val expectedLastLogin = MutableLiveData<String>()
            expectedLastLogin.value = getStringDate()

            `when`(moreMenuRepository.getActualLastLogin()).thenReturn(expectedLastLogin)

            val actualLastLogin = moreMenuViewModel.getActualLastLogin().getOrAwaitValue()

            verify(moreMenuRepository).getActualLastLogin()
            Assert.assertNotNull(actualLastLogin)
            Assert.assertEquals(expectedLastLogin.value, actualLastLogin)
        }
}