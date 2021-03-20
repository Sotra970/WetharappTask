package com.weatherapp.ui

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import com.weather.R
import com.weather.camera_feature.core.CameraControllerCallback
import com.weatherapp.domain.core.autoCleared
import com.weatherapp.domain.entities.ImageEntity
import com.weatherapp.domain.usecase.CapturePhotoUsecase
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment @Inject constructor() : Fragment()  ,
    EasyPermissions.PermissionCallbacks,
    CapturePhotoUsecase.View,
    GalleryListItemCallback ,
    CameraControllerCallback {


    private val READ_EXTERNAL_STORAGE_KEY_GET_IMGS: Int = 566
    private val READ_EXTERNAL_STORAGE_KEY_Captur_img: Int = 555

    private val TAG =  "GalleryFragment"

    var binding  by autoCleared<GalleryFragmentBinding>()

    var galleryAdapter by autoCleared<GalleryAdapter>()

    val  viewModel  by viewModels<GalleryViewModel>()

    @Inject
    lateinit var capturePhotoUsecase: CapturePhotoUsecase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        capturePhotoUsecase.register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = DataBindingUtil.inflate(inflater, R.layout.gallery_fragment, container, false)
        return binding.root
    }

    override fun  onViewCreated(view: View, savedInstanceState: Bundle?) {
        // setupBinding
        setupBinding()
        // observe to bind in adapter
        viewModel.images.observe(viewLifecycleOwner , { galleryAdapter.add(it) })
        getImages()
    }



    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        galleryAdapter=GalleryAdapter(this)
        binding.adapter = galleryAdapter
        binding.gallarycallback = this
    }

    override fun itemClick(view :View , item: ImageEntity) {
        Log.d("GalleryFragment" , item.toString())
        // save item
        viewModel.setBgImage(item.uri)
        // navigate to weathear
        val uri = Uri.parse("weatherapp://weatherdest")
        findNavController(view).navigate(uri)
    }

    override fun onCameraFail(error: String) {
        Toast.makeText(context , "sorry, error occurred while taking your image ",Toast.LENGTH_LONG).show()
    }

    override fun onCameraSaveSuccess(savedFileUri: String) {
        viewModel.getImages()
    }



    private fun requestReadExternalStorageForPreQDevices() {
        if (!hasReadExternalStoragePermission() && android.os.Build.VERSION.SDK_INT < 29 && android.os.Build.VERSION.SDK_INT >= 26){
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.read_external_storage),
                READ_EXTERNAL_STORAGE_KEY_Captur_img,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            capturePhotoUsecase.startCamera(this)
        }
    }

    private fun getImages() {
        if (!hasReadExternalStoragePermission() && android.os.Build.VERSION.SDK_INT < 29 && android.os.Build.VERSION.SDK_INT >= 26){
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.read_external_storage),
                READ_EXTERNAL_STORAGE_KEY_GET_IMGS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            viewModel.getImages()
        }
    }

    fun hasReadExternalStoragePermission(): Boolean {
        context?.let {
            return EasyPermissions.hasPermissions(
                it,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        return false
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size)

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms))
        {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
        if (requestCode==READ_EXTERNAL_STORAGE_KEY_Captur_img)
            requestReadExternalStorageForPreQDevices()
        else if (requestCode==READ_EXTERNAL_STORAGE_KEY_GET_IMGS)
            getImages()

    }

    override fun captureImage() {
        requestReadExternalStorageForPreQDevices()
    }


}