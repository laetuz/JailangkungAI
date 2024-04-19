package id.neotica.imageclassificationdemo.data.local

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import id.neotica.imageclassificationdemo.R
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.detector.Detection
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector

class ObjectDetectorHelper(
    var threshold: Float = 0.5f,
    var maxResults: Int = 5,
    val modelName: String = "efficientdet_lite0_v1.tflite",
    val context: Context,
    val detectorListener: DetectorListener?
) {
    private var objectDetector: ObjectDetector? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionBuilder.build())
        }
            .addOnSuccessListener { setupObjectDetector() }
            .addOnFailureListener {
                detectorListener?.onError(context.getString(R.string.error_tflite))
            }
    }

    private fun setupObjectDetector() {
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()

        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
            baseOptionsBuilder.useGpu()
        } else {
            baseOptionsBuilder.useNnapi()
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            detectorListener?.onError(context.getString(R.string.error))
            Log.e("Neotag", e.message.toString())
        }
    }

    fun detectObject(image: ImageProxy) {
        if (!TfLiteVision.isInitialized()) {
            Log.e("Neotag", "Error classify tflitevision is not initialized")
            detectorListener?.onError(context.getString(R.string.error_tflite))
            return
        }

        if (objectDetector == null) {
            setupObjectDetector()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(Rot90Op(-image.imageInfo.rotationDegrees / 90))
            .build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(toBitmap(image)))

        var inferenceTime = SystemClock.uptimeMillis()
        val results = objectDetector?.detect(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        detectorListener?.onResults(
            results, inferenceTime, tensorImage.height, tensorImage.width
        )
    }

    private fun toBitmap(image: ImageProxy): Bitmap {
        val bitmapBuffer = Bitmap.createBitmap(
            image.width,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        image.close()
        return bitmapBuffer
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }
}