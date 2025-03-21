package com.nurtaz.dev.selfisegmentation.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

public class PermissionCheckUtils {
    fun checkCameraPerm(context: Context,activity: Activity,process: () -> Unit){
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED){
            process()
        }else{
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),0
            )
        }
    }
    fun checkGalleryPerm(context: Context,activity: Activity,process: () -> Unit){
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED){
            process()
        }else{
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1
            )
        }
    }
    fun checkWritePerm(context: Context,activity: Activity,process: () -> Unit){
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED){
            process()
        }else{
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),2
            )
        }
    }
}