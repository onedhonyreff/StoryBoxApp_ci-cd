package com.fikri.submissionstoryappbpai.repository

import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateStoryMapResponse
import com.fikri.submissionstoryappbpai.data.faker.FakeApiService
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.utils.MainDispatcherRule
import com.fikri.submissionstoryappbpai.utils.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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
class MapsStoryRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var resources: Resources

    @Mock
    private lateinit var file: File

    @Mock
    private lateinit var latLng: LatLng

    @Mock
    private lateinit var geocoder: Geocoder

    @Mock
    private lateinit var address: Address

    @Mock
    private lateinit var pref: DataStorePreferencesInterface
    private lateinit var apiService: ApiService
    private lateinit var mapsStoryRepository: MapsStoryRepository

    @Before
    fun setup() {
        apiService = FakeApiService()
        mapsStoryRepository = MapsStoryRepository(resources, pref, geocoder, apiService)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Fetching Map Story Data Will be Success And Not Null`() =
        runTest {
            val dummyToken = flowOf("anything_token")
            val expectedResponse = generateStoryMapResponse()

            `when`(pref.getDataStoreStringValue(DataStorePreferences.TOKEN_KEY))
                .thenReturn(dummyToken)

            val actualResponse = mapsStoryRepository.getMapsStory()

            Assert.assertTrue(actualResponse is ResultWrapper.Success)
            Assert.assertNotNull(actualResponse)
            Assert.assertNotNull((actualResponse as ResultWrapper.Success).response.listStory)
            Assert.assertEquals(expectedResponse.message, actualResponse.message)
            Assert.assertEquals(
                expectedResponse.listStory.size,
                actualResponse.response.listStory.size
            )
        }

    @Test
    fun `When Fetching Map Settings, Data Is Not Null and The Return Data Will Match the Datastore`() = runTest {
        val expectedMapSetting = flowOf(DataStorePreferences.MODE_HYBRID)

        `when`(pref.getDataStoreStringValue(DataStorePreferences.MAP_MODE_KEY))
            .thenReturn(expectedMapSetting)

        val actualMapSetting = mapsStoryRepository.getMapMode().getOrAwaitValue()

        Mockito.verify(pref).getDataStoreStringValue(DataStorePreferences.MAP_MODE_KEY)
        Assert.assertNotNull(actualMapSetting)
        Assert.assertEquals(expectedMapSetting.first(), actualMapSetting)
    }

    @Test
    fun `When Calling getAddressName It Will do Reverse Geocoding and Not Null`() =
        runTest {
            val expectedAddress = "anything_address"

            @Suppress("BlockingMethodInNonBlockingContext")
            `when`(
                geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1
                )
            ).thenReturn(listOf(address))
            `when`(address.getAddressLine(0)).thenReturn(expectedAddress)
            `when`(resources.getString(R.string.location_unknown)).thenReturn("unknown")

            val actualAddress = mapsStoryRepository.getAddressName(latLng)

            Assert.assertNotNull(actualAddress)
            Assert.assertEquals(expectedAddress, actualAddress)
        }
}