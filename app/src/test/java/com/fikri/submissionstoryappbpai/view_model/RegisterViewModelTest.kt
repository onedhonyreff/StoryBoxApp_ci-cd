package com.fikri.submissionstoryappbpai.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateRegisterResponse
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.RegisterRepository
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
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var registerRepository: RegisterRepository
    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setup() {
        registerViewModel = RegisterViewModel(registerRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Registration is Successful, a Modal Response Will Appear and the Response Type Will be Successful`() =
        runTest {
            val registerResponseData = generateRegisterResponse()
            val expectedRegisterResponse =
                ResultWrapper.Success(registerResponseData, registerResponseData.message)
            val expectedIsShowResponseModal = MutableLiveData<Boolean>()
            expectedIsShowResponseModal.value = true

            Mockito.`when`(
                registerRepository.register(
                    "anything_name",
                    "anything_email",
                    "anything_password"
                )
            ).thenReturn(
                expectedRegisterResponse
            )
            registerViewModel.register(
                "anything_name",
                "anything_email",
                "anything_password"
            )

            val currentResponseType = registerViewModel.responseType
            val currentResponseMessage = registerViewModel.responseMessage
            val currentIsShowResponseModal = registerViewModel.isShowResponseModal.getOrAwaitValue()

            Mockito.verify(registerRepository).register(
                "anything_name",
                "anything_email",
                "anything_password"
            )
            Assert.assertEquals(expectedRegisterResponse.message, currentResponseMessage)
            Assert.assertEquals(ResponseModal.TYPE_SUCCESS, currentResponseType)
            Assert.assertEquals(expectedIsShowResponseModal.value, currentIsShowResponseModal)
        }

    @Test
    fun `When Executing the dismissResponseModal Method, isShowingResponseModal Becomes False`() =
        runTest {
            val expectedIsShowingResponseModal = false
            registerViewModel.dismissResponseModal()

            val actualIsShowingResponseModal =
                registerViewModel.isShowResponseModal.getOrAwaitValue()

            Assert.assertEquals(expectedIsShowingResponseModal, actualIsShowingResponseModal)
            Assert.assertFalse(actualIsShowingResponseModal)
        }
}