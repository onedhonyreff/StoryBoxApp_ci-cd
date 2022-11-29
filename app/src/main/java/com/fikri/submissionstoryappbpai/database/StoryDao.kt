package com.fikri.submissionstoryappbpai.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fikri.submissionstoryappbpai.data_model.Story

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasicStory(basicStory: List<BasicStory>)

    @Query("SELECT * FROM basic_story")
    fun getAllBasicStory(): PagingSource<Int, Story>

    @Query("SELECT COUNT(id) FROM basic_story")
    fun getBasicStoryCount(): LiveData<Int>

    @Query("DELETE FROM basic_story")
    suspend fun deleteAllBasicStory()
}