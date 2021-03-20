package com.weather.data.gateways.remote.weather.models

data class WeatherErrorResponse(
    val cod: String,
    val message: String
)