package com.weather.data.gateways.ImagesDataSource

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.weather.data.gateways.ImagesDataSource.models.CameraDataSourceModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryImagesSource @Inject constructor(
    @ApplicationContext val context: Context
) {

    /**
     * @return list of app external imagtes  as CameraDataSourceModel
     */
    suspend fun queryImageStorage():List<CameraDataSourceModel> {
        // create list of CameraDataSourceModel
        val list = mutableListOf<CameraDataSourceModel>()
        // create projection
        val imageProjection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media._ID,
        )

        // sorting order
        val imageSortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        //selection


        // make query on app external images
        val cursor = context.contentResolver.query(
            getUri(),
            imageProjection,
//                null,null,
            getSelection(),
            getSlectionArgs(),
            imageSortOrder
        )
        cursor.use {
            it?.let {
                // get id column
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                // get name column
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                // fetch all images in cursor
                while (it.moveToNext()) {
                    // get id value
                    val id = it.getLong(idColumn)
                    //get name value
                    val name = it.getString(nameColumn)
                    // create image uri
                    val contentUri = ContentUris.withAppendedId(
                        getUri(),
                        id
                    )
                    // add new child to our list
                    list.add(
                        CameraDataSourceModel(
                            name = name,
                            id = id,
                            uri = contentUri
                        )
                    )
                }
            }
        }
        return list
    }

    private fun getUri(): Uri {
       return  if (android.os.Build.VERSION.SDK_INT >= 29){
             MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }else{
           return  MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }

    private fun getSlectionArgs(): Array<String>? {
        if (android.os.Build.VERSION.SDK_INT >= 29){
            return  arrayOf("Pictures/${getAppname()}/")
        }else{
            return  arrayOf("%${getAppname()}%")
        }
    }

    private fun getSelection(): String?{
        if (android.os.Build.VERSION.SDK_INT >= 29){
          return  MediaStore.Images.Media.RELATIVE_PATH + " = ?"
        }else{
            val filter =  "${MediaStore.MediaColumns.DATA } LIKE ?"
            return filter
        }
    }

    /**
     * @return application name
     */
    private fun getAppname(): String {
        val stringId : Int = context.applicationInfo.labelRes
        return if (stringId == 0) context.applicationInfo.nonLocalizedLabel.toString()
        else context.getString(stringId)
    }
}