package com.dicoding.cekladang.ui.analisis

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.dicoding.cekladang.R
import com.dicoding.cekladang.databinding.ActivityAnalisisBinding
import com.dicoding.cekladang.helper.ImageClassifierHelper
import com.dicoding.cekladang.ui.dashboard.HistoryFragment
import com.dicoding.cekladang.ui.result.ResultActivity
import com.dicoding.cekladang.ui.utils.getImageUri

@Suppress("DEPRECATION")
class AnalisisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalisisBinding
    private var currentImageUri: Uri? = null
    private var croppedImageUri: Uri? = null

    private var plantName: String? = null
    private var labelName: String? = null
    private var modelPath: String? = null

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnalisisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        plantName = intent.getStringExtra("PLANT_NAME")
        labelName = intent.getStringExtra("LABEL_NAME")
        modelPath = intent.getStringExtra("MODEL_PATH")

        // Pastikan data diterima dengan benar
        Log.d(TAG, "Name: $plantName, Label: $labelName  Model: $modelPath")


        // Update model dan label di ImageClassifierHelper
        imageClassifierHelper =
            ImageClassifierHelper(this, object : ImageClassifierHelper.ClassifierListener {
                override fun onError(errorMessage: String) {
                    showToast(errorMessage)
                }

                override fun onResults(results: List<String>, inferenceTime: Long) {
                    // Tampilkan hasil klasifikasi
                    Log.d(TAG, "Hasil klasifikasi: $results")
                    // Lakukan tindakan selanjutnya sesuai hasil
                }
            })

        labelName?.let { label ->
            modelPath?.let { model ->
                imageClassifierHelper.updateModelAndLabels(label, model)
            }
        }

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

        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                Log.d(TAG, "Analyze button clicked with currentImageUri: $it")
                analyzeImage()
                moveToResult()
            } ?: run {
                Log.e(TAG, "Analyze button clicked but currentImageUri is null")
                showToast(getString(R.string.image_classifier_failed))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentImageUri?.let {
            outState.putString("imageUri", it.toString())
        }
    }

    private fun analyzeImage() {
        Log.d(TAG, "analyzeImage called")
        val intent = Intent(this, ResultActivity::class.java)
        croppedImageUri?.let { uri ->
            Log.d(TAG, "croppedImageUri: $uri")
            intent.putExtra(ResultActivity.IMAGE_URI, uri.toString())
            startActivityForResult(intent, REQUEST_RESULT)
        } ?: run {
            Log.e(TAG, "analyzeImage: croppedImageUri is null")
            showToast(getString(R.string.image_classifier_failed))
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
            Log.d(TAG, "showImage called with currentImageUri: $it")
            launchCropActivity(it)
            binding.previewImageView.setImageURI(it)
        } ?: run {
            Log.e(TAG, "showImage: currentImageUri is null")
        }
    }

    private fun moveToResult() {
        Log.d(TAG, "moveToResult called")
        val intent = Intent(this, ResultActivity::class.java)
        croppedImageUri?.let { uri ->
            Log.d(TAG, "moveToResult: croppedImageUri: $uri")
            intent.putExtra(ResultActivity.IMAGE_URI, uri.toString())
            intent.putExtra("LABEL_NAME", labelName)  // Kirimkan labelName
            intent.putExtra("MODEL_PATH", modelPath)  // Kirimkan modelPath
            startActivity(intent)
        } ?: run {
            Log.e(TAG, "moveToResult: croppedImageUri is null")
            showToast(getString(R.string.image_classifier_failed))
        }
    }

    private fun showCroppedImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
        croppedImageUri = uri
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
            Log.d(TAG, "Crop successful: ${cropResult.uriContent}")
            val croppedBitmap =
                BitmapFactory.decodeFile(cropResult.getUriFilePath(applicationContext, true))
            binding.previewImageView.setImageBitmap(croppedBitmap)
            croppedImageUri = cropResult.uriContent
        } else {
            Log.e(TAG, "Crop failed: ${cropResult.error}")
            showToast("Crop failed: ${cropResult.error?.message}")
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "AnalisisActivity"
        private const val REQUEST_RESULT = 1001
    }
}

