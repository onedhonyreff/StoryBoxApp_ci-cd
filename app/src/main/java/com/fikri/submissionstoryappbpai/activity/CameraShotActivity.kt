package com.fikri.submissionstoryappbpai.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.databinding.ActivityCameraShootBinding
import com.fikri.submissionstoryappbpai.other_class.createFile
import com.fikri.submissionstoryappbpai.view_model.CameraShotViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelFactory


class CameraShotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraShootBinding

    private lateinit var viewModel: CameraShotViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraShootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupAction()
    }

    private fun setupData() {
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(this)
            )[CameraShotViewModel::class.java]
    }

    private fun setupAction() {
        binding.apply {
            ivSwitch.setOnClickListener {
                switchFlashLight(reset = true, withMessage = false)
                if (viewModel.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    viewModel.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                    ivSwitch.contentDescription = resources.getString(R.string.to_rear_camera_mode)
                } else {
                    viewModel.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    ivSwitch.contentDescription = resources.getString(R.string.to_front_camera_mode)
                }
                startCamera()
            }

            ivFlash.setOnClickListener {
                switchFlashLight()
            }

            ivShoot.setOnClickListener {
                takePhoto()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.pvCameraPreview.surfaceProvider)
                }

            viewModel.apply {
                imageCapture = ImageCapture.Builder().build()

                try {
                    cameraProvider.unbindAll()
                    cam = cameraProvider.bindToLifecycle(
                        this@CameraShotActivity,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    if (viewModel.cam?.cameraInfo?.hasFlashUnit() as Boolean) {
                        flashImplement()
                    }
                } catch (exc: Exception) {
                    Toast.makeText(
                        this@CameraShotActivity,
                        resources.getString(R.string.failed_to_open_camera),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun switchFlashLight(reset: Boolean = false, withMessage: Boolean = true) {
        viewModel.apply {
            if (cam?.cameraInfo?.hasFlashUnit() as Boolean) {
                flashStateNumber += 1
                if (flashStateNumber >= flashIcon.size || reset) {
                    flashStateNumber = 0
                }
                flashImplement()
            } else {
                if (withMessage) {
                    Toast.makeText(
                        this@CameraShotActivity,
                        resources.getString(
                            R.string.flash_is_not_available,
                            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                resources.getString(R.string.main_camera_position)
                            } else {
                                resources.getString(R.string.front_camera_position)
                            }
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun flashImplement() {
        viewModel.apply {
            when (flashStateNumber) {
                0 -> {
                    imageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
                    binding.ivFlash.contentDescription = resources.getString(R.string.flash_mode)
                }
                1 -> {
                    imageCapture?.flashMode = ImageCapture.FLASH_MODE_ON
                    binding.ivFlash.contentDescription =
                        resources.getString(R.string.auto_flash_mode)
                }
                2 -> {
                    imageCapture?.flashMode = ImageCapture.FLASH_MODE_AUTO
                    binding.ivFlash.contentDescription =
                        resources.getString(R.string.flashlight_mode)
                }
                3 -> {
                    imageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
                    binding.ivFlash.contentDescription = resources.getString(R.string.no_flash_mode)
                }
                else -> {
                    imageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
                    binding.ivFlash.contentDescription = resources.getString(R.string.flash_mode)
                }
            }
            cam?.cameraControl?.enableTorch(flashStateNumber == flashIcon.size - 1)

            binding.ivFlash.setImageDrawable(
                ContextCompat.getDrawable(
                    this@CameraShotActivity,
                    flashIcon[flashStateNumber]
                )
            )
        }
    }

    private fun takePhoto() {
        val imageCapture = viewModel.imageCapture ?: return

        val photoFile = createFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraShotActivity,
                        resources.getString(R.string.failed_to_take_pictures),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra("picture", photoFile)
                    intent.putExtra(
                        "isBackCamera",
                        viewModel.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    setResult(CreateStoryActivity.CAMERA_X_RESULT, intent)
                    finish()
                }
            }
        )
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }
}