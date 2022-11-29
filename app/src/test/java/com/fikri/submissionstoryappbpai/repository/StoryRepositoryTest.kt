package com.fikri.submissionstoryappbpai.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateStoryListResponse
import com.fikri.submissionstoryappbpai.data.faker.FakeApiService
import com.fikri.submissionstoryappbpai.data.faker.FakeDataStore
import com.fikri.submissionstoryappbpai.data.faker.FakeRemoteKeysDao
import com.fikri.submissionstoryappbpai.data.faker.FakeStoryDao
import com.fikri.submissionstoryappbpai.database.AppDatabase
import com.fikri.submissionstoryappbpai.database.BasicStory
import com.fikri.submissionstoryappbpai.database.RemoteKeysDao
import com.fikri.submissionstoryappbpai.database.StoryDao
import com.fikri.submissionstoryappbpai.utils.MainDispatcherRule
import com.fikri.submissionstoryappbpai.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var pref: DataStorePreferencesInterface
    private lateinit var storyDao: StoryDao
    private lateinit var remoteKeysDao: RemoteKeysDao
    private lateinit var appDatabase: AppDatabase
    private lateinit var apiService: ApiService
    private lateinit var storyRepository: StoryRepository

    @Before
    fun setup() {
        pref = FakeDataStore()
        storyDao = FakeStoryDao()
        remoteKeysDao = FakeRemoteKeysDao()
        appDatabase = mock(AppDatabase::class.java)
        apiService = FakeApiService()

        storyRepository = StoryRepository(pref, appDatabase, apiService)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `getToken Will Return Token Value in Datastore`() = runTest {
        val expectedToken = "anything_token"
        pref.saveDataStoreValue(DataStorePreferences.TOKEN_KEY, expectedToken)

        val actualToken = storyRepository.getToken()

        Assert.assertNotNull(actualToken)
        Assert.assertEquals(expectedToken, actualToken)
    }

    @Test
    fun `getStoryCount Will Return The Number of Stories in The Database`() = runTest {
        val expectedStoryCount = 10
        val responseData = generateStoryListResponse(1, expectedStoryCount).listStory
        val listStory = arrayListOf<BasicStory>()
        responseData.forEach { data ->
            listStory.add(
                BasicStory(
                    data.id,
                    data.name,
                    data.description,
                    data.photoUrl,
                    data.createdAt
                )
            )
        }
        storyDao.insertBasicStory(listStory)

        `when`(appDatabase.storyDao()).thenReturn(storyDao)

        val actualStoryCount = storyRepository.getStoryCount().getOrAwaitValue()

        Assert.assertNotNull(actualStoryCount)
        Assert.assertEquals(expectedStoryCount, actualStoryCount)
    }

    @Test
    fun `getCurrentPagingSuccessCode Will Return Code From Datastore`() = runTest {
        val expectedCode = "anything_code"

        pref.saveDataStoreValue(DataStorePreferences.PAGING_SUCCESS_CODE_KEY, expectedCode)

        val actualCode = storyRepository.getCurrentPagingSuccessCode().getOrAwaitValue()

        Assert.assertNotNull(actualCode)
        Assert.assertEquals(expectedCode, actualCode)
    }
}