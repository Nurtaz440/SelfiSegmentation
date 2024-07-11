package com.nurtaz.dev.selfisegmentation.fragment

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.ColorUtils
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.internal.ImageUtils
import com.nurtaz.dev.selfisegmentation.utils.SegmenterOptions
import java.nio.ByteBuffer

class ImageAnalyzer {
    private lateinit var mask : ByteBuffer
    private var maskWidth : Int = 0
    private var maskHeight : Int = 0

    private fun generateMaskImage(image:Bitmap): Bitmap{
        val maskBitamp = Bitmap.createBitmap(image.width,image.height,image.config)

        for(y in 0 until maskHeight){
            for (x in 0 until maskWidth){
                val bgConfidence = ((1.0 - mask.float)*255).toInt()
                maskBitamp.setPixel(x,y, Color.argb(bgConfidence,255,255,255))
            }
        }
        mask.rewind()
        return com.nurtaz.dev.selfisegmentation.utils.ImageUtils.mergeBitmap(image,maskBitamp)

    }
    private fun generateMaskNgImage(image: Bitmap,bg:Bitmap):Bitmap{
        val bgBitmap = Bitmap.createBitmap(image.width,image.height,image.config)

        for(y in 0 until maskHeight){
            for (x in 0 until maskWidth){
                val bgConfidence = ((1.0 - mask.float)*255).toInt()
                var bgPixel = bg.getPixel(x,y)

                bgPixel = ColorUtils.setAlphaComponent(bgPixel,bgConfidence)
                bgBitmap.setPixel(x,y, bgPixel)
            }
        }
        mask.rewind()
        return com.nurtaz.dev.selfisegmentation.utils.ImageUtils.mergeBitmap(image,bgBitmap)
    }
    fun analyzie(bitmap: Bitmap?){
        bitmap!!.let {
            val image = InputImage.fromBitmap(bitmap,0)

            SegmenterOptions.imageSegmentor().process(image)
                .addOnSuccessListener { segmentationMask->
                    mask = segmentationMask.buffer
                    maskWidth = segmentationMask.width
                    maskHeight = segmentationMask.height

                    GroundInstance.getInstanse()!!.foreground = generateMaskImage(it)


                }
                .addOnFailureListener {
                    Log.e("imageAnalyze",it.message.toString())
                }
        }
    }
    fun analyzeWithBg(bitmap: Bitmap?,background:Bitmap){
        bitmap?.let {
            val image = InputImage.fromBitmap(bitmap,0)
            SegmenterOptions.imageSegmentor().process(image)
                .addOnSuccessListener { segmentationMask ->
                    mask = segmentationMask.buffer
                    maskWidth = segmentationMask.width
                    maskHeight = segmentationMask.height

                    GroundInstance.getInstanse()!!.backGround = generateMaskNgImage(it,background)
                }
                .addOnFailureListener {
                    Log.e("imageAnalyze",it.message.toString())
                }

        }
    }
}