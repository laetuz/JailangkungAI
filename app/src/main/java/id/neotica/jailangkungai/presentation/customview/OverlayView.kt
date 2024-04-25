package id.neotica.jailangkungai.presentation.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import id.neotica.jailangkungai.R
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.text.NumberFormat
import java.util.LinkedList
import kotlin.math.max

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var results: List<Detection> = LinkedList<Detection>()
    private var scaleFactor: Float = 1f

    private var bounds = Rect()

    init {
        initPaints()
    }

    private fun initPaints() {
        boxPaint.color = ContextCompat.getColor(context, R.color.neotica_main)
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = 8f

        textBackgroundPaint.color = ContextCompat.getColor(context, R.color.neotica_main_transparent)
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = ContextCompat.getColor(context, R.color.white)
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f
    }

    fun setResults(
        detectionResults: MutableList<Detection>,
        imageHeight: Int,
        imageWidth: Int
    ){
        results = detectionResults
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for (result in results) {
            val boundingBox = result.boundingBox

            val left = boundingBox.left * scaleFactor
            val top = boundingBox.top * scaleFactor
            val right = boundingBox.right * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor

            // Draw bounding box around detected objects
            val drawableRect = RectF(left, top, right, bottom)
            canvas.drawRect(drawableRect, boxPaint)

            // Create text to display alongside detected objects
            val drawableText = "${result.categories[0].label} " +
                    NumberFormat.getPercentInstance().format(result.categories[0].score)

            // Draw rect behind display text
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            canvas.drawRect(
                left,
                top,
                left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                textBackgroundPaint
            )

            // Draw text for detected object
            canvas.drawText(drawableText, left, top + bounds.height(), textPaint)
        }
    }

    fun clear() {
        boxPaint.reset()
        textBackgroundPaint.reset()
        textPaint.reset()
        invalidate()
        initPaints()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}