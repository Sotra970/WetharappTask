package com.weatherapp

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.usecase.GetUserBGUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatharViewModel  @Inject constructor(
    val getUserBGUsecase : GetUserBGUsecase
): ViewModel() {

    fun getBgImage() =  getUserBGUsecase.invoke().asLiveData()
}