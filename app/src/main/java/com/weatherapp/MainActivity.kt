package com.weatherapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.weatherapp.domain.usecase.GalleryImagesUsecase
import com.weatherapp.ui.GalleryFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNav()
        setupbottomSheet()
    }


 
    private fun setupbottomSheet() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.findNavController()

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.weather_item -> {
                    if (  bottomNavigation.selectedItemId !=   R.id.weather_item)
                    navController.navigate( Uri.parse("weatherapp://weatherdest"))
                }
                R.id.gallery_item -> {
                    if (  bottomNavigation.selectedItemId !=   R.id.gallery_item)
                    navController.navigate( Uri.parse("weatherapp://gallerydest"))
                }
            }
            return@setOnNavigationItemSelectedListener true
        }



    }
    private fun setupNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.findNavController()
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            // case navigate internaly from gallery fragment to weathear fragment , bottomNavigation  will not select the item
            if (destination.label?.contains("weathar", true) == true
            ) {
                // just wait untill navigation finish replacing fragment and check if bootom navigation select weather item which means it has been navigated internally
                bottomNavigation.postDelayed({
                    if (bottomNavigation.selectedItemId != R.id.weather_item){
                        bottomNavigation.selectedItemId = R.id.weather_item
                    }
                },500)
            }
        }
    }



}