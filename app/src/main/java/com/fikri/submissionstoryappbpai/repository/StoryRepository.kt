package com.fikri.submissionstoryappbpai.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.*
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data.BasicStoryRemoteMediator
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.database.AppDatabase
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_list.StoryListFragment
import kotlinx.coroutines.flow.first

class StoryRepository(
    private val pref: DataStorePreferencesInterface,
    private val appDatabase: AppDatabase,
    private val apiService: ApiService
) {
    suspend fun getToken(): String {
        return pref.getDataStoreStringValue(DataStorePreferences.TOKEN_KEY).first()
    }

    fun getBasicStory(token: String): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                initialLoadSize = StoryListFragment.INITIAL_LOAD_SIZE,
                pageSize = StoryListFragment.PAGE_SIZE,
                prefetchDistance = StoryListFragment.PREFETCH_DISTANCE
            ),
            remoteMediator = BasicStoryRemoteMediator(appDatabase, apiService, token, pref),
            pagingSourceFactory = {
                appDatabase.storyDao().getAllBasicStory()
            },
        ).liveData
    }

    fun getStoryCount() = appDatabase.storyDao().getBasicStoryCount()

    fun getCurrentPagingSuccessCode(): LiveData<String> {
        return pref.getDataStoreStringValue(DataStorePreferences.PAGING_SUCCESS_CODE_KEY)
            .asLiveData()
    }
}