package com.fikri.submissionstoryappbpai.repository

import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateUploadStoryResponse
import com.fikri.submissionstoryappbpai.data.faker.FakeApiService
import com.fikri.submissionstoryappbpai.data.faker.FakeDataStore
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CreateStoryRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var resources: Resources

    @Mock
    private lateinit var file: File

    private lateinit var pref: DataStorePreferencesInterface
    private lateinit var apiService: ApiService
    private lateinit var createStoryRepository: CreateStoryRepository

    @Before
    fun setup() {
        pref = FakeDataStore()
        apiService = FakeApiService()
        createStoryRepository = CreateStoryRepository(resources, pref, apiService)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When The postDataToServer Function Is Successful It Will Return Result and Not Null`() =
        runTest {
            val photo = file
            val expectedResponse = generateUploadStoryResponse()

            val actualResponse =
                createStoryRepository.postDataToServer("anything_description", photo, true)

            Assert.assertNotNull(actualResponse)
            Assert.assertTrue(actualResponse is ResultWrapper.Success)
            Assert.assertEquals(
                expectedResponse.error,
                (actualResponse as ResultWrapper.Success).response.error
            )
            Assert.assertEquals(
                expectedResponse.message,
                (actualResponse).response.message
            )
        }
}