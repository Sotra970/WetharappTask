package com.weatherapp.domain.usecase

import com.weather.data.gateways.remote.weather.models.WeatherErrorResponse
import com.weather.data.gateways.remote.weather.models.WeatherResponse
import com.weather.data.gateways.remote.weather.repo.WeatherRemoteRepo
import sotra.i.chachingdemo.api.ApiResponse
import javax.inject.Inject

/**
 * Created by sotra@altakamul.tr on 3/20/2021.
 */
class GetWeatherUsecase @Inject constructor(
     val weatherRemoteRepo : WeatherRemoteRepo
){
    suspend fun invoke(city : String): ApiResponse<WeatherResponse, WeatherErrorResponse> {
        return weatherRemoteRepo.getWeather(city)
    }
}