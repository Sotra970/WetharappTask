package com.weatherapp.core

import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.weatherapp.R

class BindingAdapterUtils {
    companion object {


        @BindingAdapter("app:viewModelNotVisible")
        @JvmStatic
        fun setViewModelNotVisible(view : View , isVisible: MutableLiveData<Boolean>) {
            if (isVisible.value == true) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }
        }

        @BindingAdapter("app:viewModelVisible")
        @JvmStatic
        fun setViewModelVisible(view : View , isVisible: MutableLiveData<Boolean>) {
            if (isVisible.value == true) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }

        @BindingAdapter("app:viewModelVisible")
        @JvmStatic
        fun setViewModelVisible(view : ViewGroup , isVisible: MutableLiveData<Boolean>) {
            if (isVisible.value == true) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }

        @BindingAdapter("app:retryCallback")
        @JvmStatic
        fun setRetryCallback(view :View, remoteRetryCallback: RemoteRetryCallback): RemoteRetryCallback {
            view.run {
                 findViewById<View>(R.id.retry).setOnClickListener {
                     remoteRetryCallback.callApi()
                 }
            }
            return remoteRetryCallback
        }

        @BindingAdapter("android:visibility")
        @JvmStatic
        fun setVisibility(view: View, value: String?) {
            view.visibility = if (value.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

    }


}