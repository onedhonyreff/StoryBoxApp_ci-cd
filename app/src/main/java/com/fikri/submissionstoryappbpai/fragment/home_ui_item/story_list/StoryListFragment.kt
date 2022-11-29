package com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_list

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.activity.StoryDetailActivity
import com.fikri.submissionstoryappbpai.adapter.ListStoryAdapter
import com.fikri.submissionstoryappbpai.adapter.LoadingStateAdapter
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.databinding.FragmentStoryListBinding
import com.fikri.submissionstoryappbpai.other_class.decideOnState
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelFactory

class StoryListFragment : Fragment() {

    companion object {
        const val INITIAL_LOAD_SIZE = 4
        const val PAGE_SIZE = 4
        const val PREFETCH_DISTANCE = 0
    }

    private var binding: FragmentStoryListBinding? = null

    private lateinit var storyListListener: StoryListListener

    private lateinit var viewModel: StoryListViewModel
    private lateinit var ctx: Context
    private lateinit var adapter: ListStoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryListBinding.inflate(inflater, container, false)
        return binding?.root as View
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupAction()
    }

    private fun setupData() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireActivity())
        )[StoryListViewModel::class.java]

        binding?.srlSwipeRefeshStoryList?.setColorSchemeColors(
            ContextCompat.getColor(
                ctx,
                R.color.secondary
            )
        )
    }

    private fun setupAction() {
        viewModel.apply {
            binding?.apply {
                adapter.addLoadStateListener { combinedLoadStates ->
                    combinedLoadStates.decideOnState(
                        adapter.itemCount,
                        showLoading = { isLoading ->
                            if (isLoading) {
                                setShowOfflineAlert(false)
                                tvErrorMsg.visibility = View.GONE
                                pbLoading.visibility = View.VISIBLE
                            } else {
                                pbLoading.visibility = View.GONE
                            }
                        },
                        adapterAnyLoadingStateLoading = { isLoading ->
                            changeAdapterLoadingState(isLoading)
                            adapterInitialLoading = isLoading
                        },
                        showEmptyState = { isEmptyInAdapter ->
                            storyAdapterIsEmpty = isEmptyInAdapter
                            syncStoryListEmptyState()
                        },
                        showError = {
                            setShowOfflineAlert(true)
                        },
                    )
                }

                cvOfflineAlert.setOnClickListener {
                    if (!adapterInitialLoading) {
                        getFreshStory()
                    }
                }

                srlSwipeRefeshStoryList.setOnRefreshListener {
                    srlSwipeRefeshStoryList.isRefreshing = false
                    getFreshStory()
                }

                rvStoryList.layoutManager = LinearLayoutManager(ctx)

                getStoryCount().observe(requireActivity()) {
                    storyCountInDatabase = it
                    syncStoryListEmptyState()
                }

                showEmptyStoryMessage.observe(requireActivity()) { isShow ->
                    if (isShow) {
                        tvErrorMsg.text = getString(R.string.empty_story_list)
                        tvErrorMsg.visibility = View.VISIBLE
                    } else {
                        tvErrorMsg.visibility = View.GONE
                    }
                }

                adapterStillLoading.observe(requireActivity()) { isLoading ->
                    srlSwipeRefeshStoryList.isEnabled = !isLoading
                }

                token.observe(requireActivity()) { token ->
                    if (token.isNotEmpty()) {
                        getStory()
                    }
                }

                currentPagingSuccessCode.observe(requireActivity()) { code ->
                    if (lastPagingSuccessCode == null) {
                        lastPagingSuccessCode = code
                    } else {
                        if (lastPagingSuccessCode != code) {
                            lastPagingSuccessCode = code
                            if (scrollToTopAfterAdapterSuccessfullyRefreshed) {
                                scrollToTopAfterAdapterSuccessfullyRefreshed = false
                                adapter.submitData(lifecycle, PagingData.empty())
                                getStory()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getStory() {
        viewModel.apply {
            binding?.apply {
                rvStoryList.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter(ctx) {
                        adapter.retry()
                    }
                )
                stories.observe(requireActivity()) { data ->
                    adapter.submitData(lifecycle, data)
                }

                adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
                    override fun onClickedItem(data: Story, imageThumbnailsView: View) {
                        val moveToStoryDetail = Intent(
                            ctx, StoryDetailActivity::class.java
                        )
                        moveToStoryDetail.putExtra(StoryDetailActivity.EXTRA_STORY, data)
                        startActivity(
                            moveToStoryDetail, ActivityOptionsCompat.makeSceneTransitionAnimation(
                                ctx as Activity, Pair(imageThumbnailsView, "image_detail")
                            ).toBundle()
                        )
                    }
                })
            }
        }
    }

    private fun setShowOfflineAlert(isShowing: Boolean = false) {
        viewModel.apply {
            binding?.apply {
                if (isShowing) {
                    offlineAlertAlpha = 1f
                    offlineAlertTranslationY = 0f
                } else {
                    offlineAlertAlpha = 0f
                    offlineAlertTranslationY = -120f
                }
                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(
                            cvOfflineAlert,
                            View.TRANSLATION_Y,
                            viewModel.offlineAlertTranslationY
                        ).setDuration(500),
                        ObjectAnimator.ofFloat(
                            cvOfflineAlert,
                            View.ALPHA,
                            viewModel.offlineAlertAlpha
                        ).setDuration(500)
                    )
                    start()
                }
            }
        }
    }

    fun getFreshStory() {
        viewModel.scrollToTopAfterAdapterSuccessfullyRefreshed = true
        adapter.refresh()
    }

    override fun onStart() {
        super.onStart()
        storyListListener.onStoryListFragReady(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        adapter = ListStoryAdapter(ctx)
        storyListListener = ctx as StoryListListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    interface StoryListListener {
        fun onStoryListFragReady(fragment: Fragment)
    }
}