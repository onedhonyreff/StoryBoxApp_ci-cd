package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.repository.HomeBottomNavRepository
import kotlinx.coroutines.launch

class HomeBottomNavViewModel(private val homeBottomNavRepository: HomeBottomNavRepository) :
    ViewModel() {

    val storyListIcon = R.drawable.ic_list
    val storyMapsIcon = R.drawable.ic_map
    val createStoryIcon = R.drawable.ic_create_story
    val moreMenuIcon = R.drawable.ic_dashboard

    private val _headerIcon = MutableLiveData(storyListIcon)
    val headerIcon: LiveData<Int> = _headerIcon

    var navController: NavController? = null
    var requestToRefreshAdapterAfterAddNewPost = false
    var requestToRefreshMapAfterAddNewPost = false
    var isSplashing = false
    var requestLat: Double = 0.0
    var requestLng: Double = 0.0

    fun changeHeaderIcon(icon: Int) {
        _headerIcon.value = icon
    }

    fun clearDataStore() {
        viewModelScope.launch {
            homeBottomNavRepository.clearDataBeforeLogout()
        }
    }
}