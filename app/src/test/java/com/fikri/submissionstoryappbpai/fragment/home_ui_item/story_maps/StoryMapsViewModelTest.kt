package com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateStoryMapResponse
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.MapsStoryRepository
import com.fikri.submissionstoryappbpai.utils.MainDispatcherRule
import com.fikri.submissionstoryappbpai.utils.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
class StoryMapsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var latLng: LatLng

    @Mock
    private lateinit var mapsStoryRepository: MapsStoryRepository
    private lateinit var storyMapsViewModel: StoryMapsViewModel

    private val mapStoryResponse = generateStoryMapResponse()
    private val expectedResponse = ResultWrapper.Success(mapStoryResponse, mapStoryResponse.message)

    @Before
    fun setup() {
        runBlocking {
            `when`(mapsStoryRepository.getMapsStory()).thenReturn(expectedResponse)
            storyMapsViewModel = StoryMapsViewModel(mapsStoryRepository)
        }
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `Since This has Called the getMapStory Method on the Repository Changing Stories`() =
        runTest {
            val actualResponseType = storyMapsViewModel.responseType
            val actualResponseMessage = storyMapsViewModel.responseMessage
            val actualResponseStories = storyMapsViewModel.stories.getOrAwaitValue()

            Mockito.verify(mapsStoryRepository).getMapsStory()
            Assert.assertEquals(ResponseModal.TYPE_SUCCESS, actualResponseType)
            Assert.assertEquals(expectedResponse.message, actualResponseMessage)
            Assert.assertNotNull(actualResponseStories)
            Assert.assertEquals(
                expectedResponse.response.listStory.size,
                actualResponseStories.size
            )
        }

    @Test
    fun `When doing reverseGeocoding It doesn't Return Null`() = runTest {
        val expectedAddress = "anything_address"

        `when`(mapsStoryRepository.getAddressName(latLng)).thenReturn(expectedAddress)

        val actualAddress = storyMapsViewModel.getAddressName(latLng)

        Mockito.verify(mapsStoryRepository).getAddressName(latLng)
        Assert.assertNotNull(actualAddress)
        Assert.assertEquals(expectedAddress, actualAddress)
    }

    @Test
    fun `When Calling dismissRefreshModal Then isShowRefreshModal Becomes False`() = runTest {
        val expectedIsShowRefreshModal = false
        storyMapsViewModel.dismissRefreshModal()

        val actualIsShowRefreshModal = storyMapsViewModel.isShowRefreshModal.getOrAwaitValue()

        Assert.assertEquals(expectedIsShowRefreshModal, actualIsShowRefreshModal)
        Assert.assertFalse(actualIsShowRefreshModal)
    }
}