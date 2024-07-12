package com.nurtaz.dev.selfisegmentation.fragment

import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.nurtaz.dev.selfisegmentation.R
import com.nurtaz.dev.selfisegmentation.databinding.FragmentStreamModeBinding
import com.nurtaz.dev.selfisegmentation.utils.DrawOverlay
import com.nurtaz.dev.selfisegmentation.utils.PermissionCheckUtils
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class StreamModeFragment : Fragment() {

    private  var _binding: FragmentStreamModeBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraProvider: ListenableFuture<ProcessCameraProvider>
    private lateinit var drawOverlay: DrawOverlay
    private lateinit var streamAnalyzer: StreamAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionCheckUtils().checkCameraPerm(requireContext(),requireActivity()){
            setUpCameraProvider()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      _binding = FragmentStreamModeBinding.inflate(layoutInflater,container,false)
        drawOverlay = binding.drawOverlay
        drawOverlay.setWillNotDraw(false)
        drawOverlay.setZOrderOnTop(true)
        streamAnalyzer = StreamAnalyzer(drawOverlay)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpCameraProvider(){
        cameraProvider = ProcessCameraProvider.getInstance(requireContext())
        cameraProvider.addListener({
            try {
                val cameraProvider : ProcessCameraProvider = cameraProvider.get()
                bindPreview(cameraProvider)
            }catch (e:Exception){
                Log.e("TAG",e.printStackTrace().toString())
            }
        },ContextCompat.getMainExecutor(requireContext()))
    }
    private fun bindPreview(cameraProvider: ProcessCameraProvider){
        val preViewview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preViewview.setSurfaceProvider(binding.previewView.surfaceProvider)
        val displayMetrics = resources.displayMetrics
        val screenSize = Size(displayMetrics.widthPixels,displayMetrics.heightPixels)

        val imageAnalyzer = ImageAnalysis.Builder()
            .setTargetResolution(screenSize)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(),streamAnalyzer)
        cameraProvider.bindToLifecycle((this as LifecycleOwner), cameraSelector,imageAnalyzer,preViewview)
    }

    override fun onStart() {
        super.onStart()
        PermissionCheckUtils().checkCameraPerm(requireContext(),requireActivity()){
            setUpCameraProvider()
        }
    }

    override fun onResume() {
        super.onResume()
        PermissionCheckUtils().checkCameraPerm(requireContext(),requireActivity()){
            setUpCameraProvider()
        }
    }
    companion object {

    }
}