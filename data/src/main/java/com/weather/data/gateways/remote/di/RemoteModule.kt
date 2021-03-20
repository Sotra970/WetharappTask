
package com.weather.data.gateways.remote.di

import com.weather.data.gateways.remote.weather.source.WeatherService
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sotra.ynab.api.NetworkResponseFactory.ApiResponseAdapterFactory
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RemoteModule {


    private val API_URL: String = "http://api.openweathermap.org/data/2.5/"

    @Singleton
    @Provides
    fun provideApiService(): WeatherService {
        return Retrofit.Builder()
                .client( provideOkHttpClient() )
                .baseUrl(API_URL)
                .addCallAdapterFactory(ApiResponseAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherService::class.java)
    }

    private fun provideOkHttpClient() =  OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                            Timber.i(message)
                        }
                }).setLevel(HttpLoggingInterceptor.Level.BODY)
            ).build()



}



