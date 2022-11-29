package com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.fikri.submissionstoryappbpai.adapter.ListStoryAdapter
import com.fikri.submissionstoryappbpai.data.dummy.DummyResponse.generateStoryListResponse
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.repository.StoryRepository
import com.fikri.submissionstoryappbpai.utils.MainDispatcherRule
import com.fikri.submissionstoryappbpai.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryListViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var storyListViewModel: StoryListViewModel
    private lateinit var dummyToken: String
    private lateinit var dummyStory: MutableList<Story>
    private lateinit var data: PagingData<Story>
    private lateinit var expectedStory: MutableLiveData<PagingData<Story>>

    @Before
    fun setup() {
        runBlocking {
            dummyToken = "anything_token"
            dummyStory = generateStoryListResponse().listStory

            data = StoryPagingSource.snapshot(dummyStory)
            expectedStory = MutableLiveData<PagingData<Story>>()
            expectedStory.value = data

            `when`(storyRepository.getToken()).thenReturn(dummyToken)
            `when`(storyRepository.getBasicStory(dummyToken)).thenReturn(expectedStory)

            storyListViewModel = StoryListViewModel(storyRepository)
        }
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `When Get Story Should Not Null and Return Success`() = runTest {
        val actualStory: PagingData<Story> = storyListViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory, differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `When Fetching Story Count Will Call getStoryCount on Repository and Not Null`() =
        runTest {
            val expectedStoryCount = MutableLiveData<Int>()
            expectedStoryCount.value = 8

            `when`(storyRepository.getStoryCount()).thenReturn(expectedStoryCount)

            val actualStoryCount = storyListViewModel.getStoryCount().getOrAwaitValue()

            Assert.assertNotNull(actualStoryCount)
            Assert.assertEquals(expectedStoryCount.value, actualStoryCount)
        }

    @Test
    fun `When Calling syncStoryListEmptyState Then showEmptyStoryMessage Will Suit the Situation`() =
        runTest {
            storyListViewModel.storyAdapterIsEmpty = true
            storyListViewModel.storyCountInDatabase = 0
            storyListViewModel.adapterInitialLoading = true

            val expectedEmptyStory =
                (storyListViewModel.storyAdapterIsEmpty &&
                        storyListViewModel.storyCountInDatabase == 0 &&
                        !storyListViewModel.adapterInitialLoading)

            storyListViewModel.syncStoryListEmptyState()

            val actualEmptyStory = storyListViewModel.showEmptyStoryMessage.getOrAwaitValue()

            Assert.assertNotNull(actualEmptyStory)
            Assert.assertEquals(expectedEmptyStory, actualEmptyStory)
        }

    @Test
    fun `When Calling changeAdapterLoadingState With True Then adapterStillLoading Will be True`() =
        runTest {
            val expectedIsLoading = true

            storyListViewModel.changeAdapterLoadingState(expectedIsLoading)

            val actualIsLoading = storyListViewModel.adapterStillLoading.getOrAwaitValue()

            Assert.assertTrue(actualIsLoading)
            Assert.assertEquals(expectedIsLoading, actualIsLoading)
        }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}

    override fun onRemoved(position: Int, count: Int) {}

    override fun onMoved(fromPosition: Int, toPosition: Int) {}

    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}