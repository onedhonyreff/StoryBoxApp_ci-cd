package com.fikri.submissionstoryappbpai.data.faker

import com.fikri.submissionstoryappbpai.database.RemoteKeys
import com.fikri.submissionstoryappbpai.database.RemoteKeysDao

class FakeRemoteKeysDao : RemoteKeysDao {
    private var remoteKeysData = mutableListOf<RemoteKeys>()

    override suspend fun insertAll(remoteKey: List<RemoteKeys>) {
        remoteKeysData.addAll(remoteKey)
    }

    override suspend fun getRemoteKeysId(id: String): RemoteKeys? {
        val remoteKeys = remoteKeysData.find { item ->
            item.id == id
        }
        return remoteKeys
    }

    override suspend fun deleteRemoteKeys() {
        remoteKeysData.clear()
    }
}