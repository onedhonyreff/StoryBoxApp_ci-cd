package com.fikri.submissionstoryappbpai.repository

import com.fikri.submissionstoryappbpai.data.DataStorePreferencesInterface
import com.fikri.submissionstoryappbpai.database.StoryDao
import com.fikri.submissionstoryappbpai.database.RemoteKeysDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeBottomNavRepository(
    private val pref: DataStorePreferencesInterface,
    private val remoteKeysDao: RemoteKeysDao,
    private val storyDao: StoryDao
) {
    suspend fun clearDataBeforeLogout() {
        withContext(Dispatchers.Main) {
            pref.clearDataStore()
            remoteKeysDao.deleteRemoteKeys()
            storyDao.deleteAllBasicStory()
        }
    }
}