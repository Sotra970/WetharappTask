package com.weatherapp.domain.usecase

import android.net.Uri
import com.weather.data.repo.UserPrefInteractor
import kotlinx.coroutines.flow.Flow
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