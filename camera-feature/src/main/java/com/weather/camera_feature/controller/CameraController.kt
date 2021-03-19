package com.weather.camera_feature.controller

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.weather.camera_feature.ui.CameraActivity
import com.weather.camera_feature.core.CameraControllerCallback
import javax.inject.Inject

/**
 * Created by sotra@altakamul.tr on 3/18/2021.
 */
/**
 */
class CameraController @Inject constructor() {
    private  var callback : CameraControllerCallback? = null
    var cameraIntent : Intent? = null
    var resultLauncher : ActivityResultLauncher<Intent>? = null

    /**
     * you have to call registerForActivityResult inside oncreate
     * because it will thow  java.lang.IllegalStateException:
     * Fragment is attempting to registerForActivityResult after being created.
     */
    fun register(context: Fragment){
        cameraIntent = Intent(context.context, CameraActivity::class.java)
        resultLauncher = context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                handleResult(result.resultCode, result.data)
        }
    }

    /**
     * fire camera activity
     */
    fun startCamera(callback : CameraControllerCallback){
        this.callback = callback
        resultLauncher?.launch(cameraIntent)
    }

    private fun handleResult( resultCode: Int, data: Intent?) {
        when(resultCode){
                CameraActivity.RESULT_OK->{
                    data?.getStringExtra(CameraActivity.SAVED_IMG_KEY)?.let{
                        callback?.onCameraSaveSuccess(it)
                    }
                }
                CameraActivity.RESULT_ERROR->{
                    data?.getStringExtra(CameraActivity.SAVED_IMG_ERROR_KEY)?.let{
                        callback?.onCameraFail(it)
                    }
                }
            }
    }

}

