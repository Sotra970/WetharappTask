package com.weather.data.gateways.remote.weather.repo

import com.weather.data.gateways.remote.weather.source.WeatherService
import dagger.MapKey
import dagger.multibindings.StringKey
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by sotra@altakamul.tr on 3/20/2021.
 */
class WeatherRemoteRepo @Inject constructor(
     val weatherService: WeatherService,
) {
    suspend fun  getWeather(city :String) = weatherService.getWeatherByCity(city )

}