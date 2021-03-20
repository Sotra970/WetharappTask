package com.weatherapp

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.weatherapp.core.RemoteRetryCallback
import com.weatherapp.domain.core.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WeatharFragment @Inject constructor() : Fragment() {

    val  viewModel  by viewModels<WeatharViewModel>()
    var binding  by autoCleared<WeatharFragmentBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = DataBindingUtil.inflate(inflater, R.layout.weathar_fragment, container, false)
        return binding.root
    }

    override fun  onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupBinding()
        // observe bg image
        viewModel.getBgImage().observe(viewLifecycleOwner , {
            Glide.with(binding.weatharBg.context)
                .load(Uri.parse(it))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply( RequestOptions().centerCrop())
                .transition( DrawableTransitionOptions().crossFade())
                .into(binding.weatharBg)
        })

        viewModel.getWeather()

    }
    companion object{
        @JvmStatic
        lateinit var  retryCallback :RemoteRetryCallback
    }

     fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
         retryCallback = object : RemoteRetryCallback() {
             override fun callApi() {
                 viewModel.getWeather()
             }
         }
        binding.callback = retryCallback
         binding.executePendingBindings()
    }

}