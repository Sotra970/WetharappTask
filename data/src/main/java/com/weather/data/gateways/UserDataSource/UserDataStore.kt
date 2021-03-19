package com.weather.data.gateways.UserDataSource

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import com.weather.data.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by sotra@altakamul.tr on 3/19/2021.
 */
@Singleton
class UserDataStore @Inject constructor(@ApplicationContext val context: Context) {
    private val dataStore = context.createDataStore(name = "user_settings")

    companion object {
        val BG_IMG = preferencesKey<String>("bg_img_uri")
    }

    suspend fun setImage(uri: Uri) {
        dataStore.edit { preferences ->
            preferences[BG_IMG] = uri.toString()
        }
    }

    fun getUserBG(): Flow<String> {
        return dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map { preference -> preference[BG_IMG] ?: getDefaultBG()}
    }

    private fun getDefaultBG(): String {
        val resources: Resources = context.resources
        val uri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.bg))
            .appendPath(resources.getResourceTypeName(R.drawable.bg))
            .appendPath(resources.getResourceEntryName(R.drawable.bg))
            .build()
        return uri.toString()
    }


}