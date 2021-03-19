package com.weatherapp.domain.usecase

import android.net.Uri
import com.weather.data.repo.UserPrefInteractor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by sotra@altakamul.tr on 3/19/2021.
 */
@Singleton
class GetUserBGUsecase @Inject constructor(
    val userPrefInteractor : UserPrefInteractor
) {
     fun  invoke (): Flow<String> {
        return userPrefInteractor.getImage()
    }
}