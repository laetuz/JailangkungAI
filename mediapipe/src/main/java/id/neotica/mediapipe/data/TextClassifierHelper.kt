package id.neotica.mediapipe.data

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.tasks.components.containers.Classifications
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.text.textclassifier.TextClassifier
import id.neotica.mediapipe.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TextClassifierHelper(
    val modelName: String = "bert_classifier.tflite",
    val context: Context,
    var classifierListener: ClassifierListener? = null
) {
    private var textClassifier: TextClassifier? = null

    init {
        initClassifier()
    }

    private fun initClassifier() {
        try {
            val optionsBuilder = TextClassifier.TextClassifierOptions.builder()

            val baseOptionsBuilder = BaseOptions.builder()
                .setModelAssetPath(modelName)
            optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

            textClassifier = TextClassifier.createFromOptions(context, optionsBuilder.build())
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.text_classifier_failed))
            Log.e("image classifier", e.message.toString())
        }
    }

    fun classify(inputText: String) {
        if (textClassifier == null) {
            initClassifier()
        }

        CoroutineScope(Dispatchers.IO).launch {
            var inferenceTime = SystemClock.uptimeMillis()
            val results = textClassifier?.classify(inputText)
            inferenceTime = SystemClock.uptimeMillis() - inferenceTime

            classifierListener?.onResults(results?.classificationResult()?.classifications(), inferenceTime)
        }
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }
}