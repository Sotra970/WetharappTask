package com.weatherapp.domain.usecase

import com.weather.data.gateways.local.ImagesDataSource.repo.GalleryImagesInteractor
import com.weatherapp.domain.entities.ImageEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by sotra@altakamul.tr on 3/18/2021.
 */
@Singleton
class GalleryImagesUsecase @Inject constructor(
    val cameraImagesRepo : GalleryImagesInteractor
) {
    suspend  fun invoke(): List<ImageEntity> {
        return cameraImagesRepo.getImages().map {
            ImageEntity(name =it.name  , id =it.id , uri= it.uri)
        }
    }
}