package com.weather.data.gateways.ImagesDataSource.models

import android.net.Uri

/**
 * Created by sotra@altakamul.tr on 3/18/2021.
 */
data class CameraDataSourceModel(
    val name:String,
    val id: Long,
    val uri: Uri
)
