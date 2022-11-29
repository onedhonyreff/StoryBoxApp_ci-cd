package com.fikri.submissionstoryappbpai.activity

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.databinding.ActivityCreateStoryBinding
import com.fikri.submissionstoryappbpai.other_class.*
import com.fikri.submissionstoryappbpai.view_model.CreateStoryViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelFactory
import java.io.File
import java.io.FileOutputStream

class CreateStoryActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private lateinit var binding: ActivityCreateStoryBinding

    private lateinit var viewModel: CreateStoryViewModel

    private val refreshModal = RefreshModal()
    private val responseModal = ResponseModal()
    private val loadingModal = LoadingModal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupAction()
    }

    private fun setupData() {
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(this)
            )[CreateStoryViewModel::class.java]

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun setupAction() {
        if (viewModel.firstAppeared) {
            viewModel.firstAppeared = false
            unSplash()
        }

        binding.apply {
            ivPickFromCamera.setOnClickListener {
                openCamera()
            }

            ivPickFromGallery.setOnClickListener {
                openFileExplore()
            }

            buttonAdd.setOnClickListener {
                viewModel.uploadStory(edAddDescription.text.toString())
            }

            edAddDescription.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                validityCheck()
            })
        }

        viewModel.apply {
            photoBitmap.observe(this@CreateStoryActivity) { bitmap ->
                binding.ivCreatePhoto.setImageBitmap(bitmap)
                validityCheck()
            }

            isAddButtonEnabled.observe(this@CreateStoryActivity) {
                binding.buttonAdd.isEnabled = it
            }

            isShowLoadingModal.observe(this@CreateStoryActivity) { isShowLoading ->
                if (isShowLoading) {
                    loadingModal.showLoadingModal(
                        this@CreateStoryActivity,
                        LoadingModal.TYPE_GENERAL,
                        resources.getString(R.string.uploading_your_story)
                    )
                } else {
                    loadingModal.dismiss()
                }
            }

            isShowRefreshModal.observe(this@CreateStoryActivity) { isShowingRefreshModal ->
                if (isShowingRefreshModal) {
                    refreshModal.showRefreshModal(
                        this@CreateStoryActivity,
                        responseType,
                        responseMessage,
                        onRefreshClicked = {
                            viewModel.dismissRefreshModal()
                            viewModel.uploadStory(binding.edAddDescription.text.toString())
                        },
                        onCloseClicked = {
                            viewModel.dismissRefreshModal()
                        }
                    )
                } else {
                    refreshModal.dismiss()
                }
            }

            isShowResponseModal.observe(this@CreateStoryActivity) { isShowingResponseModal ->
                if (isShowingResponseModal) {
                    responseModal.showResponseModal(
                        this@CreateStoryActivity,
                        responseType,
                        responseMessage
                    ) {
                        val intent = Intent()
                        intent.putExtra(
                            HomeBottomNavigationActivity.CREATE_STORY_SUCCESS,
                            true
                        )
                        setResult(HomeBottomNavigationActivity.CREATE_STORY_RESULT, intent)

                        dismissResponseModal()
                        finish()
                    }
                } else {
                    responseModal.dismiss()
                }
            }
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CAMERA_X_RESULT) {
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
            val myFile = uriToFile(selectedImg, this@CreateStoryActivity)

            viewModel.apply {
                photo = myFile
                setPhotoBitmap(BitmapFactory.decodeFile(myFile.path))
            }

            validityCheck()
        }
    }

    private fun openCamera() {
        if (allPermissionsGranted()) {
            launcherIntentCameraX.launch(
                Intent(
                    this@CreateStoryActivity,
                    CameraShotActivity::class.java
                )
            )
        } else {
            Toast.makeText(
                this@CreateStoryActivity,
                resources.getString(R.string.user_does_not_give_camera_permission),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openFileExplore() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.select_image))
        launcherIntentGallery.launch(chooser)
    }

    private fun validityCheck() {
        binding.apply {
            viewModel.setAddButtonEnable(
                edAddDescription.text.toString().trim().isNotEmpty() && viewModel.photo != null
            )
        }
    }

    private fun unSplash() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val secondaryColor = ContextCompat.getColor(this, R.color.secondary)
        val splash = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    binding.cvSplash,
                    View.SCALE_X,
                    200f
                ).setDuration(0),
                ObjectAnimator.ofFloat(
                    binding.cvSplash,
                    View.SCALE_Y,
                    200f
                ).setDuration(0),
                ObjectAnimator.ofArgb(
                    binding.vwSplash,
                    "backgroundColor",
                    secondaryColor, primaryColor
                ).setDuration(0)
            )
        }
        val disappear = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    binding.cvSplash,
                    View.SCALE_X,
                    0f
                ).setDuration(1500),
                ObjectAnimator.ofFloat(
                    binding.cvSplash,
                    View.SCALE_Y,
                    0f
                ).setDuration(1500),
                ObjectAnimator.ofArgb(
                    binding.vwSplash,
                    "backgroundColor",
                    primaryColor, secondaryColor
                ).setDuration(2000)
            )
        }
        AnimatorSet().apply {
            playSequentially(splash, disappear)
            start()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this@CreateStoryActivity,
                    resources.getString(R.string.user_does_not_give_camera_permission),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        refreshModal.dismiss()
        responseModal.dismiss()
        loadingModal.dismiss()
    }
}