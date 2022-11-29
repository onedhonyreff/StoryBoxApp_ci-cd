package com.fikri.submissionstoryappbpai.view_model

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateUploadStoryMapResponse
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.CreateStoryMapRepository
import com.fikri.submissionstoryappbpai.utils.MainDispatcherRule
import com.fikri.submissionstoryappbpai.utils.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
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
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CreateStoryMapViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var bitmap: Bitmap

    @Mock
    private lateinit var file: File

    @Mock
    private lateinit var latLng: LatLng

    @Mock
    private lateinit var createStoryMapRepository: CreateStoryMapRepository
    private lateinit var createStoryMapViewModel: CreateStoryMapViewModel

    @Before
    fun setup() {
        createStoryMapViewModel = CreateStoryMapViewModel(createStoryMapRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Uploading Story Will Call postDataToServer Function on Repository and Change Data After Success`() =
        runTest {
            val photo = file
            val position = latLng
            val uploadStoryResponse = generateUploadStoryMapResponse()
            val expectedUploadStoryResponse =
                ResultWrapper.Success(uploadStoryResponse, uploadStoryResponse.message)
            val expectedResponseType = ResponseModal.TYPE_SUCCESS

            `when`(
                createStoryMapRepository.postDataToServer("anything_description", position, photo)
            ).thenReturn(expectedUploadStoryResponse)

            createStoryMapViewModel.photo = photo
            createStoryMapViewModel.selectedPosition = position
            createStoryMapViewModel.uploadStory("anything_description")

            val actualResponseType = createStoryMapViewModel.responseType
            val actualResponseMessage = createStoryMapViewModel.responseMessage
            val actualIsShowResponseModal =
                createStoryMapViewModel.isShowResponseModal.getOrAwaitValue()

            Mockito.verify(createStoryMapRepository)
                .postDataToServer("anything_description", position, photo)

            Assert.assertNotNull(actualResponseType)
            Assert.assertNotNull(actualResponseMessage)
            Assert.assertEquals(expectedResponseType, actualResponseType)
            Assert.assertEquals(expectedUploadStoryResponse.message, actualResponseMessage)
            Assert.assertTrue(actualIsShowResponseModal)
        }

    @Test
    fun `When Fetching the Map Mode It Will be MODE_NIGHT and Not Null`() = runTest {
        val expectedMapMode = MutableLiveData<String>()
        expectedMapMode.value = DataStorePreferences.MODE_NIGHT

        `when`(createStoryMapRepository.getMapMode()).thenReturn(expectedMapMode)

        val actualMapMode = createStoryMapViewModel.getMapModeInSetting().getOrAwaitValue()

        Assert.assertNotNull(actualMapMode)
        Assert.assertEquals(expectedMapMode.value, actualMapMode)
    }

    @Test
    fun `When doing reverseGeocoding It doesn't Return Null`() = runTest {
        val expectedAddress = "anything_address"

        `when`(createStoryMapRepository.getAddressName(latLng)).thenReturn(expectedAddress)

        val actualAddress = createStoryMapViewModel.getAddressName(latLng)

        Assert.assertNotNull(actualAddress)
        Assert.assertEquals(expectedAddress, actualAddress)
    }

    @Test
    fun `When Calling the Photo Bitmap Set Will Change the Photos Bitmap Value`() = runTest {
        val expectedBitmap = bitmap
        createStoryMapViewModel.setPhotoBitmap(expectedBitmap)

        val actualBitmap = createStoryMapViewModel.photoBitmap.getOrAwaitValue()

        Assert.assertNotNull(actualBitmap)
        Assert.assertSame(expectedBitmap, actualBitmap)
        Assert.assertEquals(expectedBitmap, actualBitmap)
    }

    @Test
    fun `When Enabling AddButton Will Change isAddButtonEnabled To True`() = runTest {
        val expectedIsEnabled = true
        createStoryMapViewModel.setAddButtonEnable(expectedIsEnabled)

        val actualIsEnabled = createStoryMapViewModel.isAddButtonEnabled.getOrAwaitValue()

        Assert.assertTrue(actualIsEnabled)
        Assert.assertEquals(expectedIsEnabled, actualIsEnabled)
    }

    @Test
    fun `When Calling dismissRefreshModal Then isShowRefreshModal Becomes False`() = runTest {
        val expectedIsShowRefreshModal = false
        createStoryMapViewModel.dismissRefreshModal()

        val actualIsShowRefreshModal = createStoryMapViewModel.isShowRefreshModal.getOrAwaitValue()

        Assert.assertEquals(expectedIsShowRefreshModal, actualIsShowRefreshModal)
        Assert.assertFalse(actualIsShowRefreshModal)
    }

    @Test
    fun `When Calling dismissResponseModal Then isShowResponseModal Becomes False`() = runTest {
        val expectedIsShowResponseModal = false
        createStoryMapViewModel.dismissResponseModal()

        val actualIsShowResponseModal =
            createStoryMapViewModel.isShowResponseModal.getOrAwaitValue()

        Assert.assertEquals(expectedIsShowResponseModal, actualIsShowResponseModal)
        Assert.assertFalse(actualIsShowResponseModal)
    }
}