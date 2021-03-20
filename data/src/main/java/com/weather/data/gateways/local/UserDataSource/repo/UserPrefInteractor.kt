package com.weather.data.gateways.local.UserDataSource.repo

import android.net.Uri
import com.weather.data.gateways.local.UserDataSource.source.UserDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by sotra@altakamul.tr on 3/19/2021.
 */
@Singleton
class UserPrefInteractor @Inject constructor(
    val userDataStore: UserDataStore
)  {
    suspend fun setUserBf(bgUri: Uri) {
        userDataStore.setImage(bgUri)
    }

    fun getImage(): Flow<String> {
        return userDataStore.getUserBG()
    }

}