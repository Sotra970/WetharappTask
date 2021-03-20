package com.weather.camera_feature.ui

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.weather.camera_feature.R
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraActivity : AppCompatActivity() {
    private  var photoFile: File?=null
    private var imageCapture: ImageCapture? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }



    }



    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, getAppname()).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }



    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        const val RESULT_OK = 1
        const val RESULT_ERROR = 500
        const val SAVED_IMG_ERROR_KEY =  "saved_img_error"
        const val SAVED_IMG_KEY =  "saved_img"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

    }

    /**
     * capturing image
     */
    fun takePhoto(view: View) {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return



        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            getOutputOPtions(),
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    setResult(RESULT_ERROR, Intent().apply {
                        putExtra(SAVED_IMG_ERROR_KEY, exc.message)
                    })
                    finish()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    // If the folder selected is an external media directory, this is
                    // unnecessary but otherwise other apps will not be able to access our
                    // images unless we scan them using [MediaScannerConnection]
                    if (android.os.Build.VERSION.SDK_INT < 29){
                        val mimeType = MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(savedUri.toFile().extension)
                        MediaScannerConnection.scanFile(
                                baseContext,
                                arrayOf(savedUri.toFile().absolutePath),
                            arrayOf(mimeType)
                        ) { _, uri ->
                            Log.d("CAP_IMG", "Image capture scanned into media store: $uri")
                            setResult(RESULT_OK, Intent().apply {
                                putExtra(SAVED_IMG_KEY,uri.toString())
                            })
                            finish()
                        }
                    }else{
                        setResult(RESULT_OK, Intent().apply {
                            putExtra(SAVED_IMG_KEY,savedUri.toString())
                        })
                        finish()
                    }


                }
            })
    }

    private fun getOutputOPtions(): ImageCapture.OutputFileOptions {
       if (android.os.Build.VERSION.SDK_INT >= 29){
           // Create time-stamped output image name
           val imageName = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
           val contentValues = ContentValues().apply {
               put(MediaStore.MediaColumns.DISPLAY_NAME, imageName  )
               put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
               put(MediaStore.MediaColumns.RELATIVE_PATH,"Pictures/${getAppname()}/")
           }
           // Create output options object which contains file + metadata
           val outputOptions = ImageCapture.OutputFileOptions.Builder(
               contentResolver,
               MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
               contentValues
           ).build()
           return  outputOptions
       }else{
           // Create time-stamped output file to hold the image
            photoFile =  File(getOutputDirectory(), SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                   .format(System.currentTimeMillis()) + ".jpg")


           // Create output options object which contains file + metadata
           return  ImageCapture.OutputFileOptions.Builder(photoFile!!).build()
       }
    }


    /**
     * @return application name
     */
    private fun getAppname(): String {
        val stringId : Int = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString()
        else getString(stringId)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                setResult(RESULT_ERROR, Intent().apply {
                    putExtra(SAVED_IMG_ERROR_KEY, "permissions not granted")
                })
                finish()
            }
        }
    }

    /**
     * starting back  camera and binding it to camera view finder
     */
    private fun startCamera() {

        cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            // Enable or disable switching between cameras
            updateCameraSwitchButton()

            bindImageCapture()
        }, ContextCompat.getMainExecutor(this))



    }

    /** Declare and bind preview, capture and analysis use cases */
    private fun bindImageCapture() {

            // Get screen metrics used to setup camera for full screen resolution
            val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }

            val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

            val rotation = viewFinder.display.rotation

            // CameraProvider
            val cameraProvider = cameraProvider
                ?: throw IllegalStateException("Camera initialization failed.")

            // CameraSelector
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            // Preview
            preview = Preview.Builder()
                // We request aspect ratio but no resolution
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation
                .setTargetRotation(rotation)
                .build()

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                // We request aspect ratio but no resolution to match preview config, but letting
                // CameraX optimize for whatever specific resolution best fits our use cases
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation, we will have to call this again if rotation changes
                // during the lifecycle of this use case
                .setTargetRotation(rotation)
                .build()

            // Must unbind the use-cases before rebinding them
            cameraProvider.unbindAll()

            try {
                // A variable number of use-cases can be passed here -
                // camera provides access to CameraControl & CameraInfo
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

                // Attach the viewfinder's surface provider to preview use case
                preview?.setSurfaceProvider(viewFinder.surfaceProvider)
            } catch (exc: Exception) {
                setResult(RESULT_ERROR, Intent().apply {
                    putExtra(SAVED_IMG_ERROR_KEY, exc.message)
                })
                finish()
            }
    }



/**
 *  [androidx.camera.core.ImageAnalysisConfig] requires enum value of
 *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
 *
 *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
 *  of preview ratio to one of the provided values.
 *
 *  @param width - preview width
 *  @param height - preview height
 *  @return suitable aspect ratio
 */
private fun aspectRatio(width: Int, height: Int): Int {
    val previewRatio = max(width, height).toDouble() / min(width, height)
    if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
        return AspectRatio.RATIO_4_3
    }
    return AspectRatio.RATIO_16_9
}


    /** Enabled or disabled a button to switch cameras depending on the available cameras */
    private fun updateCameraSwitchButton() {
        try {
            switchCamerasButton.isEnabled = hasBackCamera() && hasFrontCamera()
        } catch (exception: CameraInfoUnavailableException) {
            switchCamerasButton.isEnabled = false
        }
    }

    /** Returns true if the device has an available back camera. False otherwise */
    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    /** Returns true if the device has an available front camera. False otherwise */
    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    fun cameraSwitch(view: View) {
            lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
            // Re-bind use cases to update selected camera
            bindImageCapture()
    }


}