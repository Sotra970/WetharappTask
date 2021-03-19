package com.weather.camera_feature.core
interface CameraControllerCallback {
 fun  onCameraFail(error :String)
 fun onCameraSaveSuccess(savedFileUri : String)
}