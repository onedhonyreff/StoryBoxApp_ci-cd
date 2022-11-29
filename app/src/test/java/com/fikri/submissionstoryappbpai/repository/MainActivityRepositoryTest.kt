package com.fikri.submissionstoryappbpai.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.data.faker.FakeDataStore
import com.fikri.submissionstoryappbpai.other_class.getDayDiff
import com.fikri.submissionstoryappbpai.other_class.getStringDate
import com.fikri.submissionstoryappbpai.other_class.toDate
import com.fikri.submissionstoryappbpai.utils.MainDispatcherRule
import com.fikri.submissionstoryappbpai.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainActivityRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var pref: DataStorePreferencesInterface
    private lateinit var mainActivityRepository: MainActivityRepository
    private val currentDate = getStringDate()
    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUZnR3dsbFJHT0kySHFoQWciLCJpYXQiOjE2NjYwOTQ5OTh9.TWPx1YDmsKcER49bv6zLAaRB1spLZdW0UgTr1UiBiFs"

    @Before
    fun setup() {
        pref = FakeDataStore()
        mainActivityRepository = MainActivityRepository(pref)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `Validation Will Succeed When There Is Session and Token`() = runTest {
        val expectedResult = true
        pref.saveDataStoreValue(DataStorePreferences.SESSION_KEY, currentDate)
        pref.saveDataStoreValue(DataStorePreferences.TOKEN_KEY, token)

        val actualResult = mainActivityRepository.validatingLoginSession(currentDate.toDate())

        Assert.assertEquals(expectedResult, actualResult)
        Assert.assertTrue(actualResult)
    }

    @Test
    fun `Validation Will Fail When Last Login Is More Than 3 Days`() = runTest {
        val expectedResult = false
        pref.saveDataStoreValue(DataStorePreferences.SESSION_KEY, getStringDate(-4))
        pref.saveDataStoreValue(DataStorePreferences.TOKEN_KEY, token)

        val actualResult = mainActivityRepository.validatingLoginSession(currentDate.toDate())

        Assert.assertEquals(expectedResult, actualResult)
        Assert.assertFalse(actualResult)
    }

    @Test
    fun `Validation Will Fail When There Is No Session or Token`() = runTest {
        val expectedResult = false
        pref.clearDataStore()

        val actualResult = mainActivityRepository.validatingLoginSession(currentDate.toDate())

        Assert.assertEquals(expectedResult, actualResult)
        Assert.assertFalse(actualResult)
    }

    @Test
    fun `When There Is a New Session Will Change the Difference Between the Last Login Day and Today to 0`() = runTest {
        val expectedNumberOfDaysDifference = 0
        mainActivityRepository.saveCurrentSession()

        val savedDate = pref.getDataStoreStringValue(DataStorePreferences.SESSION_KEY).first()
        val actualNumberOfDaysDifference = getDayDiff(savedDate.toDate(), getStringDate().toDate())

        Assert.assertEquals(
            expectedNumberOfDaysDifference,
            actualNumberOfDaysDifference
        )
    }

    @Test
    fun `The Theme Mode Will Match The Themes Stored in The Datastore`() = runTest {
        val expectedDarkMode = true
        pref.saveDataStoreValue(DataStorePreferences.DARK_MODE_KEY, expectedDarkMode)

        val actualDarkMode = mainActivityRepository.getThemeSettings().getOrAwaitValue()

        Assert.assertEquals(expectedDarkMode, actualDarkMode)
    }
}