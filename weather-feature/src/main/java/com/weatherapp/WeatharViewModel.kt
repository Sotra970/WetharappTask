package com.weatherapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.entities.WeatherEntity
import com.weatherapp.domain.usecase.GetUserBGUsecase
import com.weatherapp.domain.usecase.GetWeatherUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import sotra.i.chachingdemo.api.ApiResponse
import javax.inject.Inject

@HiltViewModel
class WeatharViewModel  @Inject constructor(
    val getUserBGUsecase : GetUserBGUsecase,
    val getWeatherUsecase : GetWeatherUsecase,
): ViewModel() {

    val weather: MutableLiveData<WeatherEntity>  = MutableLiveData()
    val loading = MutableLiveData<Boolean>()
    val networkStatus = MutableLiveData<Boolean>()

    fun getBgImage() =  getUserBGUsecase.invoke().asLiveData()

    fun getWeather() = viewModelScope.launch {
        networkStatus.postValue(false)
        loading.postValue(true)
        when(val response =  getWeatherUsecase.invoke("cairo")){
            is ApiResponse.Success ->{
                loading.postValue(false)
                val remoteResponse = response.body

                weather.postValue(
                    WeatherEntity(
                        day = remoteResponse.main.temp_max.toCelsius(),
                        night = remoteResponse.main.temp_min.toCelsius(),
                        feel = remoteResponse.main.feels_like.toCelsius(),
                        temperature = remoteResponse.main.temp.toCelsius(),
                        desc = remoteResponse.weather[0].main,
                        icon = remoteResponse.weather[0].icon,
                    )
                )
            }
            is ApiResponse.NetworkError -> networkStatus.postValue(true)
            else ->loading.postValue(false)
        }
    }
}

private fun Double.toCelsius(): String {
    val c = this.minus(273.15).toInt()
    return c.toString()
}
