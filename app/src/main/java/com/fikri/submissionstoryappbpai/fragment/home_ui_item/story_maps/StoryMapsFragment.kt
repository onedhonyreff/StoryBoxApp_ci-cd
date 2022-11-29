package com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_maps

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data_model.CameraMapPosition
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.databinding.FragmentStoryMapsBinding
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import com.fikri.submissionstoryappbpai.other_class.dpToPx
import com.fikri.submissionstoryappbpai.other_class.toDate
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryMapsFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val MAPVIEW_BUNDLE_KEY = "my_maps"
        val TAG: String = StoryMapsFragment::class.java.simpleName
        val INITIAL_FOCUS = LatLng(-6.342958179, 106.929503828)
    }

    private var binding: FragmentStoryMapsBinding? = null

    private lateinit var viewModel: StoryMapsViewModel
    private lateinit var ctx: Context
    private lateinit var storyMapsListener: StoryMapsListener
    private lateinit var mMapView: MapView
    private lateinit var mMap: GoogleMap

    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var commonMarkerIcon: BitmapDescriptor
    private lateinit var selectedMarkerIcon: BitmapDescriptor
    private val commonMarkerAlpha = 0.5f
    private val selectedMarkerAlpha = 1f
    private var isDetached: Boolean? = null

    private val refreshModal = RefreshModal()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryMapsBinding.inflate(inflater, container, false)
        return binding?.root as View
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            mMapView = mvStoryMaps
            initGoogleMap(savedInstanceState)
        }
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setupData()
        setupAction()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mMapView.onSaveInstanceState(mapViewBundle)
    }

    private fun setupData() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireActivity())
        )[StoryMapsViewModel::class.java]

        commonMarkerIcon = BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_AZURE
        )
        selectedMarkerIcon = BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_RED
        )

        mMap.setPadding(
            0,
            resources.getDimension(R.dimen.header_height).toInt(),
            0,
            resources.getDimension(R.dimen.bottom_nav_height).toInt()
        )

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        setElementPropertyValue()
    }

    private fun setupAction() {
        viewModel.apply {
            binding?.apply {
                val cameraPosition =
                    CameraPosition.Builder().target(currentCameraPosition.target)
                        .zoom(currentCameraPosition.zoom)
                        .bearing(currentCameraPosition.bearing).build()
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                ibToggleMapMode.setOnClickListener {
                    setShowMapModeOptions(!viewModel.isShowingMapModeOptions)
                }

                rlTypeHybrid.setOnClickListener {
                    if (viewModel.currentMapMode != DataStorePreferences.MODE_HYBRID) {
                        selectMapMode(DataStorePreferences.MODE_HYBRID)
                    }
                }

                rlTypeSatellite.setOnClickListener {
                    if (viewModel.currentMapMode != DataStorePreferences.MODE_NIGHT) {
                        selectMapMode(DataStorePreferences.MODE_NIGHT)
                    }
                }

                rlTypeNormal.setOnClickListener {
                    if (viewModel.currentMapMode != DataStorePreferences.MODE_NORMAL) {
                        selectMapMode(DataStorePreferences.MODE_NORMAL)
                    }
                }

                cvStoryItem.setOnClickListener {
                    storyMapsListener.onRequestDetailFromMap(selectedStory, ivItemPhoto)
                }

                fabRefresh.setOnClickListener {
                    getFreshMapStory()
                }

                mMap.setOnMarkerClickListener { selectedMarker ->
                    selectedMarkerObject?.setIcon(commonMarkerIcon)
                    selectedMarkerObject?.alpha = commonMarkerAlpha
                    selectedMarkerObject = selectedMarker
                    selectedMarkerObjectId = selectedMarker.snippet
                    selectedMarkerObject?.setIcon(selectedMarkerIcon)
                    selectedMarkerObject?.alpha = selectedMarkerAlpha

                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLng(
                            LatLng(
                                selectedMarker.position.latitude,
                                selectedMarker.position.longitude
                            )
                        ),
                        500, null
                    )
                    lifecycleScope.launch {
                        storyOnMap.forEach { item ->
                            if (item.id == selectedMarker.snippet) {
                                val latLng = LatLng(item.lat ?: 0.0, item.lon ?: 0.0)
                                val address = getAddressName(latLng)
                                val storyWithAddress = item.also {
                                    it.address = address
                                }
                                withContext(Dispatchers.Main) {
                                    setShowPreview(true, storyWithAddress)
                                }
                            }
                        }
                    }
                    return@setOnMarkerClickListener true
                }

                mMap.setOnMapClickListener {
                    selectedMarkerObject?.setIcon(commonMarkerIcon)
                    selectedMarkerObject?.alpha = commonMarkerAlpha
                    selectedMarkerObject = null
                    selectedMarkerObjectId = null
                    setShowPreview(false, null)
                }

                mapModeInSetting.observe(requireActivity()) { mapMode ->
                    if (currentMapMode == null) {
                        selectMapMode(mapMode)
                    } else {
                        selectMapMode(
                            currentMapMode ?: DataStorePreferences.MODE_HYBRID
                        )
                    }
                }

                stories.observe(requireActivity()) { stories ->
                    mMap.clear()
                    attachStoryOnMap(stories)
                }

                isShowLoading.observe(requireActivity()) { isLoading ->
                    if (isLoading) {
                        pbLoading.visibility = View.VISIBLE
                        fabRefresh.visibility = View.GONE
                    } else {
                        pbLoading.visibility = View.GONE
                        fabRefresh.visibility = View.VISIBLE
                    }
                }

                isShowRefreshModal.observe(requireActivity()) { isShowingRefreshModal ->
                    if (isShowingRefreshModal) {
                        refreshModal.showRefreshModal(
                            ctx,
                            responseType,
                            responseMessage,
                            onRefreshClicked = {
                                viewModel.dismissRefreshModal()
                                viewModel.getStories()
                            },
                            onCloseClicked = {
                                viewModel.dismissRefreshModal()
                            }
                        )
                    } else {
                        refreshModal.dismiss()
                    }
                }
            }
        }
    }

    private fun attachStoryOnMap(stories: MutableList<Story>) {
        viewModel.storyOnMap = ArrayList()
        stories.forEach { story ->
            if (story.lat != null && story.lon != null) {
                viewModel.storyOnMap.add(story)
                val latLng = LatLng(story.lat, story.lon)
                if (viewModel.selectedMarkerObject != null &&
                    viewModel.selectedMarkerObjectId != null &&
                    story.id == viewModel.selectedMarkerObjectId
                ) {
                    mMap.addMarker(
                        MarkerOptions().position(latLng)
                            .snippet(story.id)
                            .icon(selectedMarkerIcon)
                            .alpha(selectedMarkerAlpha)
                    ).also { newMarker ->
                        viewModel.selectedMarkerObject = newMarker
                        viewModel.selectedMarkerObjectId = story.id
                    }
                } else {
                    mMap.addMarker(
                        MarkerOptions().position(latLng)
                            .snippet(story.id)
                            .icon(commonMarkerIcon)
                            .alpha(commonMarkerAlpha)
                    )
                }
                if (story.lat > -16 && story.lat < 4 && story.lon > 97 && story.lon < 117) {
                    boundsBuilder.include(latLng)
                }
            }
        }

        if (viewModel.firstAppeared && isDetached == null) {
            viewModel.firstAppeared = false
            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels - resources.getDimension(R.dimen.header_height)
                        .toInt(),
                    resources.displayMetrics.heightPixels - resources.getDimension(R.dimen.bottom_nav_height)
                        .toInt(),
                    dpToPx(ctx, 8f).toInt()
                )
            )
        }

        storyMapsListener.onStoryMapFragReady(this)
    }

    fun getFreshMapStory(focus: LatLng? = null) {
        binding?.let {
            if (focus != null) {
                setShowPreview(false, null)
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            focus.latitude,
                            focus.longitude
                        ),
                        18f
                    ),
                    1500,
                    null
                )
                storyMapsListener.onFocusRequestExecuted()
            }
            viewModel.getStories()
        }
    }

    private fun setPreviewValue(story: Story?) {
        viewModel.apply {
            binding?.apply {
                if (story != null) {
                    Glide.with(ctx)
                        .load(story.photoUrl)
                        .error(R.drawable.default_image)
                        .into(ivItemPhoto)
                    tvItemName.text = story.name
                    tvItemName.contentDescription =
                        ctx.getString(R.string.written_by, story.name)
                    tvDescription.text = story.description
                    story.createdAt.toDate("yyyy-MM-dd'T'HH:mm:ss.SS'Z'").time.let { date ->
                        tvDate.text = ctx.getString(
                            R.string.upload_date,
                            DateUtils.getRelativeTimeSpanString(
                                date
                            ).toString()
                        )
                        tvDate.contentDescription = ctx.getString(
                            R.string.uploaded_on,
                            DateUtils.getRelativeTimeSpanString(
                                date
                            ).toString()
                        )
                    }
                }
            }
        }
    }

    private fun setShowPreview(isShowing: Boolean, story: Story? = null) {
        viewModel.apply {
            binding?.apply {
                if (story != null) {
                    setPreviewValue(story)
                    val address = getAddressName(LatLng(story.lat ?: 0.0, story.lon ?: 0.0))
                    story.address = address
                    selectedStory = story
                }

                rlStoryPreview.visibility = if (isShowing) View.VISIBLE else View.GONE
                isShowingPreview = isShowing
                previewAlpha = if (isShowing) 1f else 0f
                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(
                            rlStoryPreview,
                            View.ALPHA,
                            previewAlpha
                        ).setDuration(300)
                    )
                    start()
                }
            }
        }
    }

    private fun selectMapMode(type: String) {
        binding?.apply {
            rlTypeHybrid.background =
                ContextCompat.getDrawable(ctx, R.drawable.bg_maps_type)
            rlTypeSatellite.background =
                ContextCompat.getDrawable(ctx, R.drawable.bg_maps_type)
            rlTypeNormal.background =
                ContextCompat.getDrawable(ctx, R.drawable.bg_maps_type)
            when (type) {
                DataStorePreferences.MODE_HYBRID -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                    rlTypeHybrid.background =
                        ContextCompat.getDrawable(ctx, R.drawable.bg_maps_type_active)
                    viewModel.currentMapMode = type
                }
                DataStorePreferences.MODE_NIGHT -> {
                    if (setMapStyle(true)) {
                        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                        rlTypeSatellite.background =
                            ContextCompat.getDrawable(ctx, R.drawable.bg_maps_type_active)
                        viewModel.currentMapMode = type
                    }
                }
                DataStorePreferences.MODE_NORMAL -> {
                    if (setMapStyle(false)) {
                        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                        rlTypeNormal.background =
                            ContextCompat.getDrawable(ctx, R.drawable.bg_maps_type_active)
                        viewModel.currentMapMode = type
                    }
                }
            }
        }
    }

    private fun setShowMapModeOptions(isShowing: Boolean = false) {
        viewModel.apply {
            binding?.apply {
                isShowingMapModeOptions = isShowing
                ibToggleMapMode.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        if (isShowing) R.drawable.ic_arrow_left else R.drawable.ic_arrow_right
                    )
                )
                if (isShowing) {
                    hybridTranslationX = dpToPx(ctx, (8f + 70f))
                    satelliteTranslationX = dpToPx(ctx, (8f + (70f * 2f) + 4f))
                    normalTranslationX = dpToPx(ctx, (8f + (70f * 3f) + (4f * 2f)))
                    toggleModeTranslationX = dpToPx(ctx, (8f + (70f * 3f) + (4f * 3f)))
                } else {
                    hybridTranslationX = 0f
                    satelliteTranslationX = 0f
                    normalTranslationX = 0f
                    toggleModeTranslationX = 0f
                }
                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(
                            rlTypeHybrid,
                            View.TRANSLATION_X,
                            hybridTranslationX
                        ).setDuration(300 / 2),
                        ObjectAnimator.ofFloat(
                            rlTypeSatellite,
                            View.TRANSLATION_X,
                            satelliteTranslationX
                        ).setDuration(585 / 2),
                        ObjectAnimator.ofFloat(
                            rlTypeNormal,
                            View.TRANSLATION_X,
                            normalTranslationX
                        ).setDuration(870 / 2),
                        ObjectAnimator.ofFloat(
                            ibToggleMapMode,
                            View.TRANSLATION_X,
                            toggleModeTranslationX
                        ).setDuration(885 / 2),
                    )
                    interpolator = LinearInterpolator()
                    start()
                }
            }
        }
    }

    private fun setElementPropertyValue() {
        viewModel.apply {
            binding?.apply {
                rlTypeHybrid.translationX = hybridTranslationX
                rlTypeSatellite.translationX = satelliteTranslationX
                rlTypeNormal.translationX = normalTranslationX
                ibToggleMapMode.translationX = toggleModeTranslationX
                rlStoryPreview.alpha = previewAlpha
                rlStoryPreview.visibility = if (isShowingPreview) View.VISIBLE else View.GONE
                if (isShowingPreview && selectedStory != null) {
                    setPreviewValue(selectedStory)
                }
                ibToggleMapMode.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        if (isShowingMapModeOptions) R.drawable.ic_arrow_left else R.drawable.ic_arrow_right
                    )
                )
            }
        }
    }

    private fun setMapStyle(isNight: Boolean = false): Boolean {
        try {
            val success =
                mMap.setMapStyle(
                    if (isNight) MapStyleOptions.loadRawResourceStyle(
                        ctx,
                        R.raw.map_style
                    ) else MapStyleOptions("[]")
                )
            if (success) {
                Log.e(TAG, "Style parsing failed.")
                return true
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
        val position = mMap.cameraPosition
        viewModel.currentCameraPosition =
            CameraMapPosition(position.target, position.zoom, position.bearing)
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        storyMapsListener = ctx as StoryMapsListener
    }

    override fun onDetach() {
        super.onDetach()
        isDetached = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        refreshModal.dismiss()
    }

    interface StoryMapsListener {
        fun onStoryMapFragReady(fragment: Fragment)
        fun onRequestDetailFromMap(data: Story?, view: View)
        fun onFocusRequestExecuted()
    }
}