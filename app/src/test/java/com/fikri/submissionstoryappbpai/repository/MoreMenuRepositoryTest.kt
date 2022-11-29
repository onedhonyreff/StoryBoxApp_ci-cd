package com.fikri.submissionstoryappbpai.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.other_class.getStringDate
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
class MoreMenuRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var pref: DataStorePreferencesInterface
    private lateinit var moreMenuRepository: MoreMenuRepository

    @Before
    fun setup() {
        moreMenuRepository = MoreMenuRepository(pref)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Fetching Username Data Is Not Null`() =
        runTest {
            val expectedUsername = flowOf("Indry Puji Lestari")

            `when`(pref.getDataStoreStringValue(DataStorePreferences.NAME_KEY))
                .thenReturn(expectedUsername)

            val actualUserName = moreMenuRepository.getUserName().getOrAwaitValue()

            Mockito.verify(pref).getDataStoreStringValue(DataStorePreferences.NAME_KEY)
            Assert.assertNotNull(actualUserName)
            Assert.assertEquals(expectedUsername.first(), actualUserName)
        }

    @Test
    fun `When Fetching The Last Login Date The Data is Not Null`() =
        runTest {
            val expectedLastLoginDate = flowOf(getStringDate())

            `when`(pref.getDataStoreStringValue(DataStorePreferences.LAST_LOGIN_KEY))
                .thenReturn(expectedLastLoginDate)

            val actualLastLoginDate = moreMenuRepository.getActualLastLogin().getOrAwaitValue()

            Mockito.verify(pref).getDataStoreStringValue(DataStorePreferences.LAST_LOGIN_KEY)
            Assert.assertNotNull(actualLastLoginDate)
            Assert.assertEquals(expectedLastLoginDate.first(), actualLastLoginDate)
        }
}