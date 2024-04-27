package id.neotica.bert.data

import android.content.Context
import android.util.Log
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer
import org.tensorflow.lite.task.text.qa.QaAnswer

class BertQAHelper(
    val context: Context,
    private val resultAnswerListener: ResultAnswerListener
) {
    private var bertQuestionAnswerer: BertQuestionAnswerer? = null

    private fun setupBertQuestionAnswerer() {
        val baseOptionBuilder = BaseOptions.builder()

        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
           // baseOptionBuilder.useGpu()
        } else {
            baseOptionBuilder.useNnapi()
        }

        val options = BertQuestionAnswerer.BertQuestionAnswererOptions.builder()
            .setBaseOptions(baseOptionBuilder.build())
            .build()

        try {
            bertQuestionAnswerer = BertQuestionAnswerer.createFromFileAndOptions(context, "mobilebert.tflite", options)
        } catch (e: IllegalStateException) {
            resultAnswerListener.onError("Bert failed to be initialized")
            Log.e("neolog", "Bert failed to initialize: " + e.message)
        }
    }

    fun getQuestionAnswerer(topicsContent: String, question: String) {
        if (bertQuestionAnswerer == null) setupBertQuestionAnswerer()

        var inferenceTime = System.currentTimeMillis()
        val answers = bertQuestionAnswerer?.answer(topicsContent, question)

        inferenceTime = System.currentTimeMillis() - inferenceTime

        resultAnswerListener.onResults(answers, inferenceTime)
    }

    fun clearBert() {
        bertQuestionAnswerer?.close()
        bertQuestionAnswerer = null
    }

    interface ResultAnswerListener {
        fun onError(error: String)
        fun onResults(result: List<QaAnswer>?, inferenceTime: Long)
    }
}