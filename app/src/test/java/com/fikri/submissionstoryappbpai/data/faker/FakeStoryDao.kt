package com.fikri.submissionstoryappbpai.data.faker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.database.BasicStory
import com.fikri.submissionstoryappbpai.database.StoryDao

class FakeStoryDao : StoryDao {
    private var storyData = mutableListOf<BasicStory>()

    override suspend fun insertBasicStory(basicStory: List<BasicStory>) {
        storyData.addAll(basicStory)
    }

    override fun getAllBasicStory(): PagingSource<Int, Story> {
        val story = arrayListOf<Story>()
        storyData.forEach { data ->
            story.add(
                Story(
                    data.id,
                    data.name,
                    data.description,
                    data.photoUrl,
                    data.createdAt
                )
            )
        }

        return StoryPagingSource(story)
    }

    override fun getBasicStoryCount(): LiveData<Int> {
        val observableStoryCount = MutableLiveData<Int>()
        observableStoryCount.value = storyData.size
        return observableStoryCount
    }

    override suspend fun deleteAllBasicStory() {
        storyData.clear()
    }

    internal class StoryPagingSource(private val data: ArrayList<Story>) :
        PagingSource<Int, Story>() {
        private companion object {
            const val INITIAL_PAGE_INDEX = 1
        }

        override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
            return try {
                val position = params.key ?: INITIAL_PAGE_INDEX
                val responseData = data.slice(position..params.loadSize)
                LoadResult.Page(
                    data = responseData,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (responseData.isEmpty()) null else position + 1
                )
            } catch (exception: Exception) {
                return LoadResult.Error(exception)
            }
        }
    }
}