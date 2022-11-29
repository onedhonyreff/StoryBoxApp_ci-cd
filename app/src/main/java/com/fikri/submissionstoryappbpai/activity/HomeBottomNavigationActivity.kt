package com.fikri.submissionstoryappbpai.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.databinding.ActivityHomeBottomNavigationBinding
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.create_story_options.CreateStoryOptionsFragment
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.more_menu.MoreMenuFragment
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_list.StoryListFragment
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_maps.StoryMapsFragment
import com.fikri.submissionstoryappbpai.view_model.HomeBottomNavViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeBottomNavigationActivity : AppCompatActivity(), StoryListFragment.StoryListListener,
    StoryMapsFragment.StoryMapsListener,
    MoreMenuFragment.SettingFragListener,
    CreateStoryOptionsFragment.CreateStoryOptionsListener {

    companion object {
        const val CREATE_STORY_RESULT = 100
        const val CREATE_STORY_SUCCESS = "is_successfully_create_story"
        const val CREATE_STORY_MAP_RESULT = 110
        const val CREATE_STORY_MAP_SUCCESS = "is_successfully_create_story_map"
        const val CREATE_STORY_MAP_SUCCESS_LAT = "create_story_success_map_lat"
        const val CREATE_STORY_MAP_SUCCESS_LNG = "create_story_success_map_lng"
    }

    private lateinit var binding: ActivityHomeBottomNavigationBinding
    private lateinit var navView: BottomNavigationView

    private lateinit var viewModel: HomeBottomNavViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupAction()
    }

    private fun setupData() {
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(this)
            )[HomeBottomNavViewModel::class.java]

        navView = binding.navView
        viewModel.navController =
            findNavController(R.id.nav_host_fragment_activity_home_bottom_navigation)
        navView.setupWithNavController(viewModel.navController!!)
    }

    private fun setupAction() {
        viewModel.headerIcon.observe(this@HomeBottomNavigationActivity) { icon ->
            binding.ivHeaderIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this@HomeBottomNavigationActivity,
                    icon
                )
            )
        }
    }

    private val bottomNavListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_story_list -> {
                    viewModel.changeHeaderIcon(viewModel.storyListIcon)
                    binding.tvHeaderTitle.text = getString(R.string.story_list)
                }
                R.id.navigation_story_maps -> {
                    viewModel.changeHeaderIcon(viewModel.storyMapsIcon)
                    binding.tvHeaderTitle.text = getString(R.string.geolocation_stories)
                }
                R.id.navigation_create_story_options -> {
                    viewModel.changeHeaderIcon(viewModel.createStoryIcon)
                    binding.tvHeaderTitle.text = getString(R.string.create_a_new_story)
                }
                R.id.navigation_more_menu -> {
                    viewModel.changeHeaderIcon(viewModel.moreMenuIcon)
                    binding.tvHeaderTitle.text = getString(R.string.more_menu)
                }
            }
        }

    private val launcherIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CREATE_STORY_RESULT) {
            if (result.data?.getBooleanExtra(CREATE_STORY_SUCCESS, true) as Boolean) {
                viewModel.requestToRefreshAdapterAfterAddNewPost = true
                navView.selectedItemId = R.id.navigation_story_list
            }
        }
        if (result.resultCode == CREATE_STORY_MAP_RESULT) {
            if (result.data?.getBooleanExtra(CREATE_STORY_MAP_SUCCESS, true) as Boolean) {
                viewModel.requestLat =
                    result.data?.getDoubleExtra(CREATE_STORY_MAP_SUCCESS_LAT, 0.0) as Double
                viewModel.requestLng =
                    result.data?.getDoubleExtra(CREATE_STORY_MAP_SUCCESS_LNG, 0.0) as Double
                viewModel.requestToRefreshMapAfterAddNewPost = true
                navView.selectedItemId = R.id.navigation_story_maps
            }
        }
    }

    private fun fabSplash() {
        viewModel.isSplashing = true
        binding.apply {
            val primaryColor =
                ContextCompat.getColor(this@HomeBottomNavigationActivity, R.color.primary)
            val secondaryColor =
                ContextCompat.getColor(this@HomeBottomNavigationActivity, R.color.secondary)
            val splash = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(cvSplash, View.SCALE_X, 200f).setDuration(1500),
                    ObjectAnimator.ofFloat(cvSplash, View.SCALE_Y, 200f).setDuration(1500),
                    ObjectAnimator.ofArgb(vwSplash, "backgroundColor", secondaryColor, primaryColor)
                        .setDuration(1000)
                )
            }
            val disappear = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(cvSplash, View.SCALE_X, 0f).setDuration(0),
                    ObjectAnimator.ofFloat(cvSplash, View.SCALE_Y, 0f).setDuration(0),
                    ObjectAnimator.ofArgb(vwSplash, "backgroundColor", primaryColor, secondaryColor)
                        .setDuration(0)
                )
            }
            AnimatorSet().apply {
                playSequentially(splash, disappear)
                start()
            }
        }
    }

    override fun onStoryListFragReady(fragment: Fragment) {
        if (viewModel.requestToRefreshAdapterAfterAddNewPost) {
            val storyListFragment = fragment as StoryListFragment
            storyListFragment.getFreshStory()
            viewModel.requestToRefreshAdapterAfterAddNewPost = false
        }
    }

    override fun onStoryMapFragReady(fragment: Fragment) {
        if (viewModel.requestToRefreshMapAfterAddNewPost) {
            val storyMapFragment = fragment as StoryMapsFragment
            val requestFocus = LatLng(viewModel.requestLat, viewModel.requestLng)
            storyMapFragment.getFreshMapStory(requestFocus)
        }
    }

    override fun onFocusRequestExecuted() {
        viewModel.requestToRefreshMapAfterAddNewPost = false
    }

    override fun onRequestDetailFromMap(data: Story?, view: View) {
        if (data != null) {
            val moveToStoryDetail = Intent(
                this@HomeBottomNavigationActivity, StoryDetailActivity::class.java
            )
            moveToStoryDetail.putExtra(StoryDetailActivity.EXTRA_STORY, data)
            startActivity(
                moveToStoryDetail, ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, Pair(view, "image_detail")
                ).toBundle()
            )
        }
    }

    override fun onCreateStoryOptionsClicked(options: String) {
        if (!viewModel.isSplashing) {
            when (options) {
                CreateStoryOptionsFragment.CREATE_BASIC_STORY -> {
                    fabSplash()
                    lifecycleScope.launch(Dispatchers.Default) {
                        delay(800)
                        withContext(Dispatchers.Main) {
                            viewModel.isSplashing = false
                            launcherIntent.launch(
                                Intent(
                                    this@HomeBottomNavigationActivity,
                                    CreateStoryActivity::class.java
                                )
                            )
                        }
                    }
                }
                CreateStoryOptionsFragment.CREATE_GEOLOCATION_STORY -> {
                    launcherIntent.launch(
                        Intent(
                            this@HomeBottomNavigationActivity,
                            CreateStoryMapActivity::class.java
                        )
                    )
                }
            }
        }
    }

    override fun onOptionClicked(item: String) {
        when (item) {
            MoreMenuFragment.APPLICATION_DETAILS_OPTIONS -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            MoreMenuFragment.DISPLAY_OPTIONS -> {
                startActivity(
                    Intent(
                        this@HomeBottomNavigationActivity,
                        DisplayConfigurationActivity::class.java
                    )
                )
            }
        }
    }

    override fun onLogoutRequest() {
        viewModel.clearDataStore()
        finish()
        startActivity(Intent(this@HomeBottomNavigationActivity, LoginActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        viewModel.navController?.addOnDestinationChangedListener(bottomNavListener)
    }

    override fun onPause() {
        super.onPause()
        viewModel.navController?.removeOnDestinationChangedListener(bottomNavListener)
    }
}