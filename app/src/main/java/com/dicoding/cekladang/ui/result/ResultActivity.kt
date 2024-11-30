package com.dicoding.cekladang.ui.result

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cekladang.MainActivity
import com.dicoding.cekladang.databinding.ActivityResultBinding
import com.dicoding.cekladang.helper.ImageClassifierHelper

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = "Hasil Analisis"
        }

        val imageUriString = intent.getStringExtra(IMAGE_URI)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            displayImage(imageUri)

            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(errorMessage: String) {
                        Log.d(TAG, "Error: $errorMessage")
                    }

                    override fun onResults(results: List<String>, inferenceTime: Long) {
                        showResults(results)
                    }
                }
            )
            imageClassifierHelper.init()
            val bitmap = uriToBitmap(imageUri)
            if (bitmap != null) {
                imageClassifierHelper.classifyImage(bitmap)
            } else {
                Log.e(TAG, "Failed to convert Uri to Bitmap")
            }
        } else {
            Log.e(TAG, "No image URI provided")
            finish()
        }

        binding.btnSave.setOnClickListener {
            Toast.makeText(this, "Data Tersimpan", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

    private fun displayImage(uri: Uri) {
        Log.d(TAG, "Displaying image: $uri")
        binding.resultImage.setImageURI(uri)
    }

    private fun showResults(results: List<String>) {
        if (results.isNotEmpty()) {
            val label = results[0] // Mengambil hasil klasifikasi terbaik
            binding.resultText.text = label
            Log.d(TAG, "Result: $label")
        } else {
            Log.e(TAG, "No classification results")
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

//    private fun savePredictionToDatabase(imageUri: Uri, result: String) {
//        if (result.isNotEmpty()) {
//            val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
//            val destinationUri = Uri.fromFile(File(cacheDir, fileName))
//            contentResolver.openInputStream(imageUri)?.use { input ->
//                FileOutputStream(File(cacheDir, fileName)).use { output ->
//                    input.copyTo(output)
//                }
//            }
//            val prediction =
//                PredictionHistory(imagePath = destinationUri.toString(), result = result)
//            GlobalScope.launch(Dispatchers.IO) {
//                val database = AppDatabase.getDatabase(applicationContext)
//                try {
//                    database.predictionHistoryDao().insertPrediction(prediction)
//                    Log.d(TAG, "Prediction saved successfully: $prediction")
//                    val predictions = database.predictionHistoryDao().getAllPredictions()
//                    Log.d(TAG, "All predictions after save: $predictions")
//                    moveToHistory(destinationUri, result)
//                } catch (e: Exception) {
//                    Log.e(TAG, "Failed to save prediction: $prediction", e)
//                }
//            }
//        } else {
//            Log.e(TAG, "Result is empty, cannot save prediction to database.")
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    companion object {
        const val IMAGE_URI = "img_uri"
        const val TAG = "imagePicker"
        const val RESULT_TEXT = "result_text"
        const val REQUEST_HISTORY_UPDATE = 1
    }
}