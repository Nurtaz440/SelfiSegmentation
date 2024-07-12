package com.nurtaz.dev.selfisegmentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.nurtaz.dev.selfisegmentation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var navController: NavController

    private lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>
    private var isCameraGranted = false
    private var isGalleryReadGranted = false
    private var isGalleryWriteGranted = false

    private var isStream = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Navigation
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
    }
}