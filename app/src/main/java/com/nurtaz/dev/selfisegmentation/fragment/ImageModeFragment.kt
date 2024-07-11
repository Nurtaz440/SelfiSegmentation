package com.nurtaz.dev.selfisegmentation.fragment

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.core.graphics.drawable.toBitmap
import com.nurtaz.dev.selfisegmentation.R
import com.nurtaz.dev.selfisegmentation.databinding.FragmentImageModeBinding
import com.nurtaz.dev.selfisegmentation.utils.ImageUtils
import com.nurtaz.dev.selfisegmentation.utils.LoadingDialog
import com.nurtaz.dev.selfisegmentation.utils.PermissionCheckUtils


class ImageModeFragment : Fragment(),OnClickListener {
    private var _binding : FragmentImageModeBinding? = null
    private val binding get() = _binding!!

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var imageViewUri : Uri
    private lateinit var selectedBitmap : Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImageModeBinding.inflate(inflater,container,false)
        loadingDialog = LoadingDialog(requireActivity(),inflater)
        binding.apply {
            chip.isCheckable = true
            chip.setOnCheckedChangeListener { button, isChecked ->
                if (isChecked){
                    button.text = "Foreground Mode"
                    mode = !mode
                }else{
                    button.text = "Background Mode"
                    mode = !mode
                }
            }
            btnSegment.setOnClickListener(this@ImageModeFragment)
            btnCamera.setOnClickListener(this@ImageModeFragment)
            btnAdd.setOnClickListener(this@ImageModeFragment)
            btnBAck.setOnClickListener(this@ImageModeFragment)
            btnDownload.setOnClickListener(this@ImageModeFragment)


        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
    var mode : Boolean = true
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnSegment->{
                loadingDialog.show()
                ImageAnalyzer().analyzie(binding.ivSelfi.drawable.toBitmap())
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.ivSelfi.setImageBitmap(GroundInstance.getInstanse()?.foreground)
                },2000)
            }
            R.id.btnCamera->{
                PermissionCheckUtils().checkCameraPerm(requireContext(),requireActivity()){
                    imageViewUri = ImageUtils.getImageUriFromBitmap(requireContext(),binding.ivSelfi.drawable.toBitmap())
                    takePhotoResult.launch(imageViewUri)
                }
            }
            R.id.btnAdd->{
                PermissionCheckUtils().checkGalleryPerm(requireContext(),requireActivity()){
                    selectImageFromGallery()
                }
            }
            R.id.btnBAck->{
                binding.ivSelfi.setImageResource(0)
                binding.ivSelfi.setImageBitmap(GroundInstance.getInstanse()?.backImage)
            }
            R.id.btnDownload->{
                PermissionCheckUtils().checkWritePerm(requireContext(),requireActivity()){
                    ImageUtils.download(binding.ivSelfi.drawable.toBitmap(),requireContext())
                }
            }
        }
    }
    private fun createSelfiWithBckground(uri:Uri?){
        if (GroundInstance.getInstanse()?.foreground != null){
            binding.ivSelfi.setImageResource(0)
            selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,uri)

            val resizeBitmap = ImageUtils.resizeBitmap(
                selectedBitmap,
                GroundInstance.getInstanse()?.foreground!!.width,
                GroundInstance.getInstanse()?.foreground!!.height
            )

            ImageAnalyzer().analyzeWithBg(
                GroundInstance.getInstanse()?.foreground!!,
                resizeBitmap
            )

            Handler(Looper.getMainLooper()).postDelayed({
                binding.ivSelfi.setImageBitmap(GroundInstance.getInstanse()?.backGround)
            },2000)

        }else{
            Toast.makeText(requireContext(),"Make a segment selfi before choosing a background",Toast.LENGTH_SHORT).show()
            loadingDialog.dismiss()
        }

    }
    private val takePhotoResult = registerForActivityResult(ActivityResultContracts.TakePicture()){
        if (it){
            imageViewUri.let {uri->
                if (mode){
                    binding.ivSelfi.setImageResource(0)
                    binding.ivSelfi.setImageURI(uri)
                    GroundInstance.getInstanse()?.backGround = binding.ivSelfi.drawable.toBitmap()
                    binding.btnBAck.visibility = View.VISIBLE

                }else{
                    loadingDialog.show()
                    binding.ivSelfi.setImageResource(0)
                    createSelfiWithBckground(uri)
                }

            }
        }
    }
    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")
    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()){ uri->

        uri.let {
                if (mode){
                    binding.ivSelfi.setImageResource(0)
                    binding.ivSelfi.setImageURI(uri)
                    GroundInstance.getInstanse()?.backGround = binding.ivSelfi.drawable.toBitmap()
                    binding.btnBAck.visibility = View.VISIBLE

                }else{
                    loadingDialog.show()
                    createSelfiWithBckground(uri)
                }

            }

    }
}