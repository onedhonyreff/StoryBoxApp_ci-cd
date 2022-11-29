package com.fikri.submissionstoryappbpai.activity

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.custom_component.WorkaroundMapFragment
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.data_model.CameraMapPosition
import com.fikri.submissionstoryappbpai.databinding.ActivityCreateStoryMapBinding
import com.fikri.submissionstoryappbpai.other_class.*
import com.fikri.submissionstoryappbpai.view_model.CreateStoryMapViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class CreateStoryMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_CAMERA_PERMISSIONS = 10
        val TAG: String = CreateStoryMapActivity::class.java.simpleName
        val INITIAL_FOCUS = LatLng(-6.342958179, 106.929503828)
    }

    private lateinit var binding: ActivityCreateStoryMapBinding

    private lateinit var viewModel: CreateStoryMapViewModel
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap

    private val refreshModal = RefreshModal()
    private val loadingModal = LoadingModal()
    private val responseModal = ResponseModal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_input) as WorkaroundMapFragment
        (supportFragmentManager.findFragmentById(R.id.map_input) as WorkaroundMapFragment).setListener(
            object : WorkaroundMapFragment.OnTouchListener {
                override fun onTouch() {
                    binding.svWrapper.requestDisallowInterceptTouchEvent(true)
                }
            })
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setupData()
        setupAction()
    }

    private fun setupData() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[CreateStoryMapViewModel::class.java]

        mMap.setPadding(
            0,
            dpToPx(this, 50f).toInt(),
            0,
            0
        )

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setElementPropertyValue()
    }

    private fun setupAction() {
        viewModel.apply {
            binding.apply {
                val cameraPosition =
                    CameraPosition.Builder().target(currentCameraPosition.target)
                        .zoom(currentCameraPosition.zoom)
                        .bearing(currentCameraPosition.bearing).build()
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                ibToggleMapMode.setOnClickListener {
                    setShowMapModeOptions(!isShowingMapModeOptions)
                }

                rlTypeHybrid.setOnClickListener {
                    if (currentMapMode != DataStorePreferences.MODE_HYBRID) {
                        choiceMapMode(DataStorePreferences.MODE_HYBRID)
                    }
                }

                rlTypeSatellite.setOnClickListener {
                    if (currentMapMode != DataStorePreferences.MODE_NIGHT) {
                        choiceMapMode(DataStorePreferences.MODE_NIGHT)
                    }
                }

                rlTypeNormal.setOnClickListener {
                    if (currentMapMode != DataStorePreferences.MODE_NORMAL) {
                        choiceMapMode(DataStorePreferences.MODE_NORMAL)
                    }
                }

                mMap.setOnCameraIdleListener {
                    mMap.cameraPosition.target.let {
                        lifecycleScope.launch {
                            val position = LatLng(it.latitude, it.longitude)
                            val address = getAddressName(position)
                            selectedPosition = position
                            validityCheck()
                            withContext(Dispatchers.Main) {
                                tvAddressPreview.text = address
                            }
                        }
                    }
                }

                ivPickFromCamera.setOnClickListener {
                    if (!allPermissionsGranted()) {
                        ActivityCompat.requestPermissions(
                            this@CreateStoryMapActivity,
                            REQUIRED_PERMISSIONS,
                            REQUEST_CODE_CAMERA_PERMISSIONS
                        )
                    } else {
                        openCamera()
                    }
                }

                ivPickFromGallery.setOnClickListener {
                    openFileExplore()
                }

                edAddDescription.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                    validityCheck()
                })

                buttonAdd.setOnClickListener {
                    viewModel.uploadStory(edAddDescription.text.toString())
                }

                getMapModeInSetting().observe(this@CreateStoryMapActivity) { mapMode ->
                    if (currentMapMode == null) {
                        choiceMapMode(mapMode)
                    } else {
                        choiceMapMode(
                            currentMapMode ?: DataStorePreferences.MODE_HYBRID
                        )
                    }
                }

                photoBitmap.observe(this@CreateStoryMapActivity) { bitmap ->
                    ivCreatePhoto.setImageBitmap(bitmap)
                    validityCheck()
                }

                isAddButtonEnabled.observe(this@CreateStoryMapActivity) {
                    buttonAdd.isEnabled = it
                }

                isShowLoading.observe(this@CreateStoryMapActivity) { isShowLoading ->
                    if (isShowLoading) {
                        loadingModal.showLoadingModal(
                            this@CreateStoryMapActivity,
                            LoadingModal.TYPE_GENERAL,
                            resources.getString(R.string.uploading_your_story)
                        )
                    } else {
                        loadingModal.dismiss()
                    }
                }

                isShowRefreshModal.observe(this@CreateStoryMapActivity) { isShowingRefreshModal ->
                    if (isShowingRefreshModal) {
                        refreshModal.showRefreshModal(
                            this@CreateStoryMapActivity,
                            responseType,
                            responseMessage,
                            onRefreshClicked = {
                                dismissRefreshModal()
                                uploadStory(binding.edAddDescription.text.toString())
                            },
                            onCloseClicked = {
                                dismissRefreshModal()
                            }
                        )
                    } else {
                        refreshModal.dismiss()
                    }
                }

                isShowResponseModal.observe(this@CreateStoryMapActivity) { isShowingResponseModal ->
                    if (isShowingResponseModal) {
                        responseModal.showResponseModal(
                            this@CreateStoryMapActivity,
                            responseType,
                            responseMessage
                        ) {
                            val intent = Intent()
                            intent.putExtra(
                                HomeBottomNavigationActivity.CREATE_STORY_MAP_SUCCESS,
                                true
                            )
                            intent.putExtra(
                                HomeBottomNavigationActivity.CREATE_STORY_MAP_SUCCESS_LAT,
                                selectedPosition?.latitude
                            )
                            intent.putExtra(
                                HomeBottomNavigationActivity.CREATE_STORY_MAP_SUCCESS_LNG,
                                selectedPosition?.longitude
                            )
                            setResult(HomeBottomNavigationActivity.CREATE_STORY_MAP_RESULT, intent)

                            dismissResponseModal()
                            finish()
                        }
                    } else {
                        responseModal.dismiss()
                    }
                }

                getMyLocation()
            }
        }
    }

    private fun validityCheck() {
        binding.apply {
            viewModel.setAddButtonEnable(
                edAddDescription.text.toString().trim().isNotEmpty() &&
                        viewModel.photo != null &&
                        viewModel.selectedPosition != null
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CreateStoryActivity.CAMERA_X_RESULT) {
            val myFile = result.data?.getSerializableExtra("picture") as File
            val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val fixImage = rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)

            fixImage.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(myFile))

            viewModel.apply {
                photo = myFile
                setPhotoBitmap(fixImage)
            }

            validityCheck()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@CreateStoryMapActivity)

            viewModel.apply {
                photo = myFile
                setPhotoBitmap(BitmapFactory.decodeFile(myFile.path))
            }

            validityCheck()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun openCamera() {
        if (allPermissionsGranted()) {
            launcherIntentCameraX.launch(
                Intent(
                    this@CreateStoryMapActivity,
                    CameraShotActivity::class.java
                )
            )
        } else {
            Toast.makeText(
                this@CreateStoryMapActivity,
                resources.getString(R.string.user_does_not_give_camera_permission),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openFileExplore() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.select_image))
        launcherIntentGallery.launch(chooser)
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun choiceMapMode(type: String) {
        binding.apply {
            rlTypeHybrid.background =
                ContextCompat.getDrawable(this@CreateStoryMapActivity, R.drawable.bg_maps_type)
            rlTypeSatellite.background =
                ContextCompat.getDrawable(this@CreateStoryMapActivity, R.drawable.bg_maps_type)
            rlTypeNormal.background =
                ContextCompat.getDrawable(this@CreateStoryMapActivity, R.drawable.bg_maps_type)
            when (type) {
                DataStorePreferences.MODE_HYBRID -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                    rlTypeHybrid.background =
                        ContextCompat.getDrawable(
                            this@CreateStoryMapActivity,
                            R.drawable.bg_maps_type_active
                        )
                    viewModel.currentMapMode = type
                }
                DataStorePreferences.MODE_NIGHT -> {
                    if (setMapStyle(true)) {
                        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                        rlTypeSatellite.background =
                            ContextCompat.getDrawable(
                                this@CreateStoryMapActivity,
                                R.drawable.bg_maps_type_active
                            )
                        viewModel.currentMapMode = type
                    }
                }
                DataStorePreferences.MODE_NORMAL -> {
                    if (setMapStyle(false)) {
                        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                        rlTypeNormal.background =
                            ContextCompat.getDrawable(
                                this@CreateStoryMapActivity,
                                R.drawable.bg_maps_type_active
                            )
                        viewModel.currentMapMode = type
                    }
                }
            }
        }
    }

    private fun setShowMapModeOptions(isShowing: Boolean = false) {
        viewModel.apply {
            binding.apply {
                isShowingMapModeOptions = isShowing
                ibToggleMapMode.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@CreateStoryMapActivity,
                        if (isShowing) R.drawable.ic_arrow_left else R.drawable.ic_arrow_right
                    )
                )
                if (isShowing) {
                    hybridTranslationX = dpToPx(this@CreateStoryMapActivity, (8f + 70f))
                    satelliteTranslationX =
                        dpToPx(this@CreateStoryMapActivity, (8f + (70f * 2f) + 4f))
                    normalTranslationX =
                        dpToPx(this@CreateStoryMapActivity, (8f + (70f * 3f) + (4f * 2f)))
                    toggleModeTranslationX =
                        dpToPx(this@CreateStoryMapActivity, (8f + (70f * 3f) + (4f * 3f)))
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
            binding.apply {
                rlTypeHybrid.translationX = hybridTranslationX
                rlTypeSatellite.translationX = satelliteTranslationX
                rlTypeNormal.translationX = normalTranslationX
                ibToggleMapMode.translationX = toggleModeTranslationX
                ibToggleMapMode.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@CreateStoryMapActivity,
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
                        this,
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSIONS) {
            openCamera()
        }
    }

    override fun onStop() {
        super.onStop()
        val position = mMap.cameraPosition
        viewModel.currentCameraPosition =
            CameraMapPosition(position.target, position.zoom, position.bearing)
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingModal.dismiss()
        responseModal.dismiss()
        refreshModal.dismiss()
    }
}