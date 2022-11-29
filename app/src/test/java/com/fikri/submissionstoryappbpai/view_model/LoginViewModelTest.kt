package com.fikri.submissionstoryappbpai.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateLoginResponse
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.repository.LoginRepository
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var loginRepository: LoginRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setup() {
        loginViewModel = LoginViewModel(loginRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Login is Successful It Will Save the Session and the Instructions to Home Change to True`() =
        runTest {
            val loginResponseData = generateLoginResponse()
            val expectedLoginResponse =
                ResultWrapper.Success(loginResponseData, loginResponseData.message)
            val expectedIsTimeToHome = MutableLiveData<Boolean>()
            expectedIsTimeToHome.value = true

            `when`(loginRepository.login("anything_email", "anything_password")).thenReturn(
                expectedLoginResponse
            )
            loginViewModel.login("anything_email", "anything_password")

            val currentLoginResponse = loginViewModel.loginResponse
            val currentIsTimeToHome = loginViewModel.isTimeToHome.getOrAwaitValue()

            Mockito.verify(loginRepository).login("anything_email", "anything_password")
            Mockito.verify(loginRepository).saveLoginData(currentLoginResponse?.loginResult)
            Assert.assertSame(expectedLoginResponse.response, currentLoginResponse)
            Assert.assertEquals(expectedIsTimeToHome.value, currentIsTimeToHome)
        }

    @Test
    fun `When Executing the dismissResponseModal Method, isShowingResponseModal Becomes False`() =
        runTest {
            val expectedIsShowingResponseModal = false
            loginViewModel.dismissResponseModal()

            val actualIsShowingResponseModal = loginViewModel.isShowResponseModal.getOrAwaitValue()

            Assert.assertEquals(expectedIsShowingResponseModal, actualIsShowingResponseModal)
            Assert.assertFalse(actualIsShowingResponseModal)
        }
}