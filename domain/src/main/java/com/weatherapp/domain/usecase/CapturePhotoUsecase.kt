package com.weatherapp.domain.usecase

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.weather.camera_feature.controller.CameraController
import com.weather.camera_feature.core.CameraControllerCallback
import javax.inject.Inject

/**
 * Created by sotra@altakamul.tr on 3/19/2021.
 */
class CapturePhotoUsecase @Inject constructor(
    val cameraController: CameraController
) {
    interface View {
        fun captureImage()
    }

    fun startCamera( callback: CameraControllerCallback){
        cameraController.startCamera(callback )
    }

    fun register(context: Fragment) {
        cameraController.register(context)
    }
}