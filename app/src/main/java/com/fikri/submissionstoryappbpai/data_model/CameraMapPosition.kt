package com.fikri.submissionstoryappbpai.data_model

import com.google.android.gms.maps.model.LatLng

data class CameraMapPosition(
    val target: LatLng,
    val zoom: Float,
    val bearing: Float
)