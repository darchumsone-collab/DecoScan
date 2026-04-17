package com.darchums.decoscan.ml

import android.content.Context
import android.graphics.Bitmap
import com.darchums.decoscan.domain.model.MaterialType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.MappedByteBuffer

class TFLiteClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val modelPath = "model.tflite"
    private val labels = listOf("Plastic", "Glass", "Paper", "Metal")

    init {
        try {
            val model: MappedByteBuffer = FileUtil.loadMappedFile(context, modelPath)
            val options = Interpreter.Options().apply {
                setNumThreads(4)
            }
            interpreter = Interpreter(model, options)
        } catch (e: Exception) {
            interpreter = null
        }
    }

    data class Recognition(
        val material: MaterialType,
        val confidence: Float
    )

    /**
     * Runs inference on the provided bitmap.
     * Uses withContext(Dispatchers.Default) to ensure thread-safety and non-blocking execution.
     */
    suspend fun classify(bitmap: Bitmap): Recognition = withContext(Dispatchers.Default) {
        if (interpreter == null) {
            return@withContext fallbackClassify(bitmap)
        }

        // 1. Preprocessing: Resize and Normalize (0-1 float range)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0.0f, 255.0f)) // Normalizes [0, 255] to [0, 1]
            .build()

        var tensorImage = TensorImage(org.tensorflow.lite.DataType.FLOAT32)
        tensorImage.load(bitmap)
        tensorImage = imageProcessor.process(tensorImage)

        // 2. Inference
        val output = Array(1) { FloatArray(labels.size) }
        interpreter?.run(tensorImage.buffer, output)

        // 3. Post-processing
        val results = output[0]
        val maxIndex = results.indices.maxByOrNull { results[it] } ?: -1
        
        if (maxIndex != -1 && results[maxIndex] > 0.3f) {
            Recognition(
                material = MaterialType.fromString(labels[maxIndex]),
                confidence = results[maxIndex]
            )
        } else {
            fallbackClassify(bitmap)
        }
    }

    private fun fallbackClassify(bitmap: Bitmap): Recognition {
        val width = bitmap.width
        val height = bitmap.height
        var r = 0L; var g = 0L; var b = 0L
        val pixelCount = (width * height).toLong()

        // Sampling pixels for a deterministic heuristic
        for (y in 0 until height step 20) {
            for (x in 0 until width step 20) {
                val pixel = bitmap.getPixel(x, y)
                r += (pixel shr 16) and 0xFF
                g += (pixel shr 8) and 0xFF
                b += pixel and 0xFF
            }
        }

        val avgR = (r * 400 / pixelCount).toInt()
        val avgG = (g * 400 / pixelCount).toInt()
        val avgB = (b * 400 / pixelCount).toInt()

        return when {
            avgB > avgR && avgB > avgG -> Recognition(MaterialType.GLASS, 0.65f)
            avgR > avgG && avgR > avgB -> Recognition(MaterialType.PLASTIC, 0.60f)
            avgG > avgR && avgG > avgB -> Recognition(MaterialType.METAL, 0.55f)
            else -> Recognition(MaterialType.PAPER, 0.50f)
        }
    }
}
