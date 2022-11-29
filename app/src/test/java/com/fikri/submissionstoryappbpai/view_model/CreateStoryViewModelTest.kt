package com.fikri.submissionstoryappbpai.view_model

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateUploadStoryResponse
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.CreateStoryRepository
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
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CreateStoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var bitmap: Bitmap

    @Mock
    private lateinit var file: File

    @Mock
    private lateinit var createStoryRepository: CreateStoryRepository
    private lateinit var createStoryViewModel: CreateStoryViewModel

    @Before
    fun setup() {
        createStoryViewModel = CreateStoryViewModel(createStoryRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Uploading Story Will Call postDataToServer Function on Repository and Change Data After Success`() =
        runTest {
            val photo = file
            val uploadStoryResponse = generateUploadStoryResponse()
            val expectedUploadStoryResponse =
                ResultWrapper.Success(uploadStoryResponse, uploadStoryResponse.message)
            val expectedResponseType = ResponseModal.TYPE_SUCCESS

            `when`(
                createStoryRepository.postDataToServer("anything_description", photo)
            ).thenReturn(expectedUploadStoryResponse)

            createStoryViewModel.photo = photo
            createStoryViewModel.uploadStory("anything_description")

            val actualResponseType = createStoryViewModel.responseType
            val actualResponseMessage = createStoryViewModel.responseMessage
            val actualIsShowResponseModal =
                createStoryViewModel.isShowResponseModal.getOrAwaitValue()

            Mockito.verify(createStoryRepository).postDataToServer("anything_description", photo)

            Assert.assertNotNull(actualResponseType)
            Assert.assertNotNull(actualResponseMessage)
            Assert.assertEquals(expectedResponseType, actualResponseType)
            Assert.assertEquals(expectedUploadStoryResponse.message, actualResponseMessage)
            Assert.assertTrue(actualIsShowResponseModal)
        }

    @Test
    fun `When Calling the Photo Bitmap Set Will Change the Photos Bitmap Value`() = runTest {
        val expectedBitmap = bitmap
        createStoryViewModel.setPhotoBitmap(expectedBitmap)

        val actualBitmap = createStoryViewModel.photoBitmap.getOrAwaitValue()

        Assert.assertNotNull(actualBitmap)
        Assert.assertSame(expectedBitmap, actualBitmap)
        Assert.assertEquals(expectedBitmap, actualBitmap)
    }

    @Test
    fun `When Enabling AddButton Will Change isAddButtonEnabled to True`() = runTest {
        val expectedIsEnabled = true
        createStoryViewModel.setAddButtonEnable(expectedIsEnabled)

        val actualIsEnabled = createStoryViewModel.isAddButtonEnabled.getOrAwaitValue()

        Assert.assertTrue(actualIsEnabled)
        Assert.assertEquals(expectedIsEnabled, actualIsEnabled)
    }

    @Test
    fun `When Calling dismissRefreshModal Then isShowRefreshModal Becomes False`() = runTest {
        val expectedIsShowRefreshModal = false
        createStoryViewModel.dismissRefreshModal()

        val actualIsShowRefreshModal = createStoryViewModel.isShowRefreshModal.getOrAwaitValue()

        Assert.assertEquals(expectedIsShowRefreshModal, actualIsShowRefreshModal)
        Assert.assertFalse(actualIsShowRefreshModal)
    }

    @Test
    fun `When Calling dismissResponseModal Then isShowResponseModal Becomes False`() = runTest {
        val expectedIsShowResponseModal = false
        createStoryViewModel.dismissResponseModal()

        val actualIsShowResponseModal = createStoryViewModel.isShowResponseModal.getOrAwaitValue()

        Assert.assertEquals(expectedIsShowResponseModal, actualIsShowResponseModal)
        Assert.assertFalse(actualIsShowResponseModal)
    }
}