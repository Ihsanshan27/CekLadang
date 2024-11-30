package com.dicoding.cekladang.helper

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.Collections
import kotlin.math.min

class ImageClassifierHelper(
    private val context: Activity,
    private val classifierListener: ClassifierListener,
) {
    private var imageSizeX = 256
    private var imageSizeY = 256

    private var labels: List<String>? = null
    private var tfLite: Interpreter? = null

    private var inputImageBuffer: TensorImage? = null
    private var outputProbabilityBuffer: TensorBuffer? = null
    private var probabilityProcessor: TensorProcessor? = null

    private val label = "corn_labels.txt"
    private val model = "corn_model_with_metadata.tflite"

    fun init() {
        try {
            val opt = Interpreter.Options()
            tfLite = Interpreter(loadModelFile(context)!!, opt)
            Log.d(TAG, "Model berhasil diinisialisasi")

            checkTensors()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Model gagal diinisialisasi: ${e.message}")
        }
    }

    private fun checkTensors() {
        // Mengecek input tensor
        val inputTensorCount = tfLite!!.inputTensorCount
        for (i in 0 until inputTensorCount) {
            val tensor = tfLite!!.getInputTensor(i)
            Log.d(TAG, "Input Tensor[$i]:")
            Log.d(TAG, "\tShape = ${tensor.shape().contentToString()}")
            Log.d(TAG, "\tDataType = ${tensor.dataType()}")
            Log.d(TAG, "\tNumber of dimensions = ${tensor.numDimensions()}")
            Log.d(TAG, "\tByte size = ${tensor.numBytes()}")
            Log.d(TAG, "\tType of elements in tensor = ${tensor.dataType()}")
            Log.d(TAG, "\tTensor index = $i")
        }

        // Mengecek output tensor
        val outputTensorCount = tfLite!!.outputTensorCount
        for (i in 0 until outputTensorCount) {
            val tensor = tfLite!!.getOutputTensor(i)
            Log.d(TAG, "Output Tensor[$i]:")
            Log.d(TAG, "\tShape = ${tensor.shape().contentToString()}")
            Log.d(TAG, "\tDataType = ${tensor.dataType()}")
            Log.d(TAG, "\tNumber of dimensions = ${tensor.numDimensions()}")
            Log.d(TAG, "\tByte size = ${tensor.numBytes()}")
            Log.d(TAG, "\tType of elements in tensor = ${tensor.dataType()}")
            Log.d(TAG, "\tTensor index = $i")
        }
    }

    private fun loadImage(bitmap: Bitmap): TensorImage {
        inputImageBuffer!!.load(bitmap)

        val cropSize = min(bitmap.width, bitmap.height)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(127.5f, 127.5f))
            .build()  // Hanya resize gambar, tanpa normalisasi
        return imageProcessor.process(inputImageBuffer)
    }

    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity?): MappedByteBuffer? {
        val modelName = model
        val fileDescriptor = activity!!.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classifyImage(bitmap: Bitmap) {
        try {
            val imageTensorIndex = 0
            val imageShape = tfLite!!.getInputTensor(imageTensorIndex).shape()

            imageSizeY = imageShape[1]
            imageSizeX = imageShape[2]

            val imageDataType = tfLite!!.getInputTensor(imageTensorIndex).dataType()
            val probabilityTensorIndex = 0
            val probabilityShape = tfLite!!.getOutputTensor(probabilityTensorIndex).shape()
            val probabilityDataType = tfLite!!.getOutputTensor(probabilityTensorIndex).dataType()

            inputImageBuffer = TensorImage(imageDataType)
            outputProbabilityBuffer =
                TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)

            // Pastikan ada pemrosesan normalisasi jika diperlukan oleh model
            probabilityProcessor = TensorProcessor.Builder()
                .add(NormalizeOp(0f, 1f))  // Sesuaikan dengan normalisasi yang sesuai
                .build()

            inputImageBuffer = loadImage(bitmap)
            tfLite!!.run(inputImageBuffer!!.buffer, outputProbabilityBuffer!!.buffer.rewind())

            val results = showResult() ?: emptyList()
            classifierListener.onResults(results, 0L) // Simulasikan inferenceTime
            Log.d(TAG, "Klasifikasi berhasil dijalankan")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Klasifikasi gagal dijalankan: ${e.message}")
        }
    }

    fun showResult(): List<String>? {
        labels = try {
            FileUtil.loadLabels(context, label)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "label gagal dimuat ${e.message}")
            return null
        }
        val labeledProbability = TensorLabel(
            labels!!, probabilityProcessor!!.process(outputProbabilityBuffer)
        ).mapWithFloatValue

        labeledProbability.forEach { (key, value) ->
            val rounded = String.format("%.6f", value)
            Log.d(TAG, "Label: $key, Probability: $rounded ")
        }

        val totalScore = labeledProbability.values.sum()
        Log.d(TAG, "Total skor dari semua probabilitas: $totalScore")

        val maxValueInMap = Collections.max(labeledProbability.values)
        Log.d(TAG, "Nilai Tertinggi di antara probabilitas: $maxValueInMap")

        val minValueInMap = Collections.min(labeledProbability.values)
        Log.d(TAG, "Nilai terendah di antara probabilitas: $minValueInMap")

        val result: MutableList<String> = ArrayList()
        for ((key, value) in labeledProbability) {
            if (value == maxValueInMap) {
                result.add(key)
            }
        }
        Log.d(TAG, "Hasil klasifikasi : $result")
        return result
    }

    interface ClassifierListener {
        fun onError(errorMessage: String)
        fun onResults(results: List<String>, inferenceTime: Long)
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }

}