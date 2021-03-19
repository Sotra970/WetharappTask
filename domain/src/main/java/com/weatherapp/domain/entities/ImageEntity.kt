package com.weatherapp.domain.entities

import android.net.Uri

data class ImageEntity(
    val name:String,
    val id: Long,
    val uri: Uri
)
