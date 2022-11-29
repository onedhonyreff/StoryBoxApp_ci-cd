package com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.data_model.CameraMapPosition
import com.fikri.submissionstoryappbpai.data_model.ResultWrapper
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.MapsStoryRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.launch

class StoryMapsViewModel(private val mapsStoryRepository: MapsStoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<MutableList<Story>>()
    val stories: LiveData<MutableList<Story>> = _stories
    private val _isShowLoading = MutableLiveData(false)
    val isShowLoading: LiveData<Boolean> = _isShowLoading
    private val _isShowRefreshModal = MutableLiveData<Boolean>()
    val isShowRefreshModal: LiveData<Boolean> = _isShowRefreshModal

    val mapModeInSetting = mapsStoryRepository.getMapMode()

    var currentCameraPosition = CameraMapPosition(StoryMapsFragment.INITIAL_FOCUS, 5f, 0f)
    var storyOnMap = ArrayList<Story>()
    var hybridTranslationX = 0f
    var satelliteTranslationX = 0f
    var normalTranslationX = 0f
    var toggleModeTranslationX = 0f
    var previewAlpha = 0f
    var selectedStory: Story? = null

    var selectedMarkerObject: Marker? = null
    var selectedMarkerObjectId: String? = null
    var isShowingMapModeOptions = false
    var isShowingPreview = false

    var responseType = RefreshModal.TYPE_GENERAL
    var responseMessage: String? = null
    var currentMapMode: String? = null
    var firstAppeared = true

    init {
        getStories()
    }

    fun getStories() {
        viewModelScope.launch {
            _isShowLoading.value = true
            val result = mapsStoryRepository.getMapsStory()
            _isShowLoading.value = false

            when (result) {
                is ResultWrapper.Success -> {
                    responseType = ResponseModal.TYPE_SUCCESS
                    responseMessage = result.message
                    _stories.value = result.response.listStory
                }
                is ResultWrapper.Error -> {
                    responseType = result.failedType.toString()
                    responseMessage = result.message
                    _isShowRefreshModal.value = true
                }
                is ResultWrapper.NetworkError -> {
                    responseType = result.failedType.toString()
                    responseMessage = result.message
                    _isShowRefreshModal.value = true
                }
            }
        }
    }

    fun getAddressName(latLng: LatLng) = mapsStoryRepository.getAddressName(latLng)

    fun dismissRefreshModal() {
        _isShowRefreshModal.value = false
    }
}