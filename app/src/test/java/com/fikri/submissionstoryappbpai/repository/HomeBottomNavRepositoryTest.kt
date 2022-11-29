package com.fikri.submissionstoryappbpai.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.database.RemoteKeysDao
import com.fikri.submissionstoryappbpai.database.StoryDao
import com.fikri.submissionstoryappbpai.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
class HomeBottomNavRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var pref: DataStorePreferencesInterface

    @Mock
    private lateinit var remoteKeysDao: RemoteKeysDao

    @Mock
    private lateinit var storyDao: StoryDao

    private lateinit var homeBottomNavRepository: HomeBottomNavRepository

    @Before
    fun setup() {
        homeBottomNavRepository = HomeBottomNavRepository(pref, remoteKeysDao, storyDao)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When clearDataBeforeLogout Will Call Session Data, storyDao and remoteKeysDao Functions`() =
        runTest {
            val expectedNumberOfCall = 1
            homeBottomNavRepository.clearDataBeforeLogout()

            Mockito.verify(pref).clearDataStore()
            Mockito.verify(pref, times(expectedNumberOfCall)).clearDataStore()
            Mockito.verify(remoteKeysDao).deleteRemoteKeys()
            Mockito.verify(remoteKeysDao, times(expectedNumberOfCall)).deleteRemoteKeys()
            Mockito.verify(storyDao).deleteAllBasicStory()
            Mockito.verify(storyDao, times(expectedNumberOfCall)).deleteAllBasicStory()
        }
}