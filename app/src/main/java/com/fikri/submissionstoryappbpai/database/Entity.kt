package com.fikri.submissionstoryappbpai.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "basic_story")
data class BasicStory(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
)

@Entity(tableName = "basic_story_remote_keys")
data class RemoteKeys(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)