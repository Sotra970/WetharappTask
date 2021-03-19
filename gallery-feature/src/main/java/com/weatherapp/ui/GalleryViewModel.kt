package com.weatherapp.ui

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.entities.ImageEntity
import com.weatherapp.domain.usecase.GalleryImagesUsecase
import com.weatherapp.domain.usecase.SetUserBGUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class GalleryViewModel @Inject constructor(
    val galleryImagesUsecase: GalleryImagesUsecase ,
    val setUserBGUsecase : SetUserBGUsecase
) : ViewModel() {

    val images: MutableLiveData<List<ImageEntity>> = MutableLiveData()

    fun getImages() = viewModelScope.launch {
           images.postValue( galleryImagesUsecase.invoke())
    }

    fun setBgImage(bg: Uri)=viewModelScope.launch {
        setUserBGUsecase.invoke(bg)
    }

}