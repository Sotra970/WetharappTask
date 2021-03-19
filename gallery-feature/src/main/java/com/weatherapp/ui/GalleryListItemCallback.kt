package com.weatherapp.ui

import android.view.View
import com.weatherapp.domain.entities.ImageEntity

interface  GalleryListItemCallback {
    fun itemClick(view : View , item :ImageEntity)
}