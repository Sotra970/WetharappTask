package com.weather.data.repo

import com.weather.data.gateways.ImagesDataSource.GalleryImagesSource
import com.weather.data.gateways.ImagesDataSource.models.CameraDataSourceModel
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