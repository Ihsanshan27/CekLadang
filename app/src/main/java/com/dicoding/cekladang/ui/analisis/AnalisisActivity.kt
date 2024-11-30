package com.dicoding.cekladang.ui.analisis

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.dicoding.cekladang.databinding.ActivityAnalisisBinding
import com.dicoding.cekladang.ui.dashboard.HistoryFragment
import com.dicoding.cekladang.ui.utils.getImageUri

class AnalisisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalisisBinding
    private var currentImageUri: Uri? = null
    private var result: String? = null
//    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnalisisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?.let {
            val uriString = it.getString("imageUri")
            if (uriString != null) {
                currentImageUri = Uri.parse(uriString)
                showImage()
            }
        }

        val data = Intent(this, HistoryFragment::class.java)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
//        binding.analyzeButton.setOnClickListener {
//            currentImageUri?.let {
//                analyzeImage(data)
//            } ?: run {
//                showToast(getString(R.string.empty_image_warning))
//            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentImageUri?.let {
            outState.putString("imageUri", it.toString())
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            launchCropActivity(it)
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun launchCropActivity(uri: Uri) {
        cropActivityResultLauncher.launch(
            CropImageContractOptions(
                uri = uri,
                cropImageOptions = CropImageOptions(
                    guidelines = CropImageView.Guidelines.ON
                )
            )
        )
    }

    private var cropActivityResultLauncher = registerForActivityResult(
        CropImageContract()
    ) { cropResult: CropImageView.CropResult ->
        if (cropResult.isSuccessful) {
            val croppedBitmap =
                BitmapFactory.decodeFile(cropResult.getUriFilePath(applicationContext, true))
            binding.previewImageView.setImageBitmap(croppedBitmap)
            currentImageUri = cropResult.uriContent
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        currentImageUri?.let { uri ->
            launcherIntentCamera.launch(uri)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun analyzeImage() {

    }

}

