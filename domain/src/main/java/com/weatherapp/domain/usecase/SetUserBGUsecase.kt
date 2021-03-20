package com.weatherapp.domain.usecase

import android.net.Uri
import com.weather.data.gateways.local.UserDataSource.repo.UserPrefInteractor
import javax.inject.Inject

/**
 * Created by sotra@altakamul.tr on 3/19/2021.
 */
class SetUserBGUsecase @Inject constructor(
    val userPrefInteractor : UserPrefInteractor
) {
     suspend fun  invoke (bg : Uri) {
         userPrefInteractor.setUserBf(bg)
    }
}