package com.fikri.submissionstoryappbpai.view_model

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import com.fikri.submissionstoryappbpai.R

class CameraShotViewModel :
    ViewModel() {

    var imageCapture: ImageCapture? = null
    var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    var cam: Camera? = null
    var flashStateNumber = 0

    val flashIcon = arrayOf(
        R.drawable.ic_flash_off,
        R.drawable.ic_flash_on,
        R.drawable.ic_flash_auto,
        R.drawable.ic_flash_torch
    )
}