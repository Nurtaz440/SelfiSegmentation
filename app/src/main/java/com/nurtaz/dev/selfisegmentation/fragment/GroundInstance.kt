package com.nurtaz.dev.selfisegmentation.fragment

import android.graphics.Bitmap

class GroundInstance {
    var backImage : Bitmap? = null
    var foreground : Bitmap? = null
    var backGround : Bitmap? = null

    companion object{
        private var instance : GroundInstance? = null
        fun getInstanse() : GroundInstance?{
            if (instance == null) instance = GroundInstance()
            return instance
        }
    }
}