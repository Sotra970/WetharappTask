package com.weather.data.gateways.remote.weather.source

import com.weather.data.gateways.remote.weather.models.WeatherErrorResponse
import com.weather.data.gateways.remote.weather.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
import sotra.i.chachingdemo.api.ApiResponse

/**
 * Created by sotra@altakamul.tr on 3/20/2021.
 */
interface WeatherService {

    @GET("weather?appid=401a7a23d3877de527e77b5069fb44ae")
    suspend fun getWeatherByCity(
        @Query("q")  city : String,
    ) : ApiResponse<WeatherResponse, WeatherErrorResponse>


}