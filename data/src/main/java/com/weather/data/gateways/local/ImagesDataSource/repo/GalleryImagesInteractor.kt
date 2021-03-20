package com.weather.data.gateways.local.ImagesDataSource.repo

import com.weather.data.gateways.local.ImagesDataSource.source.GalleryImagesSource
import com.weather.data.gateways.local.ImagesDataSource.models.CameraDataSourceModel
import javax.inject.Inject

/**
 * Created by sotra@altakamul.tr on 3/19/2021.
 */

class GalleryImagesInteractor @Inject constructor(
    val cameraImageSource : GalleryImagesSource
) {
     suspend fun getImages(): List<CameraDataSourceModel> {
        return cameraImageSource.queryImageStorage()
    }
}