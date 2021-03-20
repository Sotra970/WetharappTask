package com.weatherapp;

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created by sotra@altakamul.tr on 3/19/2021.
 */

@HiltAndroidApp
class WeatherApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG && Timber.treeCount()==0)
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return   element.className + ": " +  element.methodName + ": " + element.lineNumber
                }
            })
    }
}