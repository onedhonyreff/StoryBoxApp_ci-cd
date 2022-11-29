package com.fikri.submissionstoryappbpai.repository

import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateLoginResponse
import com.fikri.submissionstoryappbpai.data.faker.FakeApiService
import com.fikri.submissionstoryappbpai.data.faker.FakeDataStore
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var resources: Resources
    private lateinit var pref: DataStorePreferencesInterface
    private lateinit var loginRepository: LoginRepository

    @Before
    fun setup() {
        pref = FakeDataStore()
        val apiService = FakeApiService()
        loginRepository = LoginRepository(resources, pref, apiService)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `The Login Function Will Return Result and Not Null`() = runTest {
        val expectedResponse = generateLoginResponse()

        val actualResponse = loginRepository.login("anything_gmail", "anything_password")

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResultWrapper.Success)
        Assert.assertEquals(
            expectedResponse.loginResult.userId,
            (actualResponse as ResultWrapper.Success).response.loginResult.userId
        )
        Assert.assertEquals(
            expectedResponse.loginResult.name,
            (actualResponse).response.loginResult.name
        )
        Assert.assertEquals(
            expectedResponse.loginResult.token,
            (actualResponse).response.loginResult.token
        )
    }

    @Test
    fun `The Save Session Function Will Save Token, Session and Other Data`() = runTest {
        val expectedLoginResult = generateLoginResponse().loginResult

        loginRepository.saveLoginData(expectedLoginResult)

        val actualUserId = pref.getDataStoreStringValue(DataStorePreferences.USER_ID_KEY).first()
        val actualToken = pref.getDataStoreStringValue(DataStorePreferences.TOKEN_KEY).first()
        val actualName = pref.getDataStoreStringValue(DataStorePreferences.NAME_KEY).first()
        val actualSession = pref.getDataStoreStringValue(DataStorePreferences.SESSION_KEY).first()
        val actualLastLogin =
            pref.getDataStoreStringValue(DataStorePreferences.LAST_LOGIN_KEY).first()

        Assert.assertNotNull(actualUserId)
        Assert.assertNotNull(actualToken)
        Assert.assertNotNull(actualName)
        Assert.assertNotNull(actualSession)
        Assert.assertNotNull(actualLastLogin)
        Assert.assertEquals(expectedLoginResult.userId, actualUserId)
        Assert.assertEquals(expectedLoginResult.token, actualToken)
        Assert.assertEquals(expectedLoginResult.name, actualName)
    }
}
