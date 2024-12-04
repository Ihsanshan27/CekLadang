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
import androidx.lifecycle.ViewModelProvider
import com.dicoding.cekladang.MainActivity
import com.dicoding.cekladang.data.local.entity.History
import com.dicoding.cekladang.databinding.ActivityResultBinding
import com.dicoding.cekladang.helper.ImageClassifierHelper
import com.dicoding.cekladang.repository.HistoryRepository
import com.dicoding.cekladang.ui.ViewModelFactory
import com.dicoding.cekladang.ui.history.HistoryViewModel

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    private var plantName: String? = null
    private var labelName: String? = null
    private var modelPath: String? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var historyViewModel: HistoryViewModel
    private var history: History = History()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = "Hasil Analisis"
        }

        val historyRepository = HistoryRepository(this)
        val factory = ViewModelFactory.getInstance(historyRepository)
        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        plantName = intent.getStringExtra("PLANT_NAME")
        labelName = intent.getStringExtra("LABEL_NAME")
        modelPath = intent.getStringExtra("MODEL_PATH")

        Log.d(TAG, "NameResult: $plantName, Label: $labelName  Model: $modelPath")

        // Cek apakah imageClassifierHelper sudah diinisialisasi
        if (!::imageClassifierHelper.isInitialized) {
            imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(errorMessage: String) {
                        Log.d(TAG, "Error: $errorMessage")
                    }

                    override fun onResults(results: List<String>, inferenceTime: Long) {
                        showResults(results)
                        if (results.isNotEmpty()) {
                            val label = results[0]
                            history.name = plantName
                            history.prediction = label
                            history.image = intent.getStringExtra(IMAGE_URI)
                            Log.d(TAG, "name: $plantName")
                        }
                    }
                }
            )
            // Update model dan label hanya sekali
            if (labelName != null && modelPath != null) {
                imageClassifierHelper.updateModelAndLabels(labelName!!, modelPath!!)
                imageClassifierHelper.init()
            }
        }

        if (labelName != null && modelPath != null) {
            Log.d(TAG, "Received label: $labelName and model path: $modelPath")
            val imageUriString = intent.getStringExtra(IMAGE_URI)
            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                displayImage(imageUri)

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
        } else {
            Log.e(TAG, "Missing label or model path")
            finish()
        }

        binding.btnSave.setOnClickListener {
            Log.d(TAG, "Saving History: ${history.name}, ${history.prediction}, ${history.image}")

            historyViewModel.insert(history)
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
        const val TAG = "AnalisisActivity"
        const val RESULT_TEXT = "result_text"
        const val REQUEST_HISTORY_UPDATE = 1
    }
}