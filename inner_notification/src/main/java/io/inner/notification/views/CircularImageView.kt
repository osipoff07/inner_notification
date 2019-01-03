package io.inner.notification.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.Shader
import android.graphics.BitmapShader
import android.graphics.ColorFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import io.inner.notif.taisty_notification.R

private val DEFAULT_BORDER_WIDTH = 4f
private val DEFAULT_SHADOW_RADIUS = 8.0f

enum class ShadowGravity {
    CENTER,
    TOP,
    BOTTOM,
    START,
    END;

    val value: Int
        get() {
            when (this) {
                CENTER -> return 1
                TOP -> return 2
                BOTTOM -> return 3
                START -> return 4
                END -> return 5
            }
            throw IllegalArgumentException("Not value available for this ShadowGravity: " + this)
        }

    companion object {

        fun fromValue(value: Int): ShadowGravity {
            when (value) {
                1 -> return CENTER
                2 -> return TOP
                3 -> return BOTTOM
                4 -> return START
                5 -> return END
            }
            throw IllegalArgumentException("This value is not supported for ShadowGravity: " + value)
        }
    }

}

class CircularImageView
@JvmOverloads
constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): AppCompatImageView(context, attrs, defStyleAttr) {

    // Properties
    private var borderWidth: Float = 0.toFloat()
    private var radius: Float = 0.toFloat()
    private var canvasSize: Int = 0
    private var shadowRadius: Float = 0.toFloat()
    private var shadowColor = Color.BLACK
    private var shadowGravity = ShadowGravity.BOTTOM
    private var myColorFilter: ColorFilter? = null

    // Object used to draw
    private var image: Bitmap? = null
    private var myDrawable: Drawable? = null
    private var paint: Paint? = null
    private var paintBorder: Paint? = null
    private var paintBackground: Paint? = null

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        // Init paint
        paint = Paint()
        paint!!.isAntiAlias = true

        paintBorder = Paint()
        paintBorder!!.isAntiAlias = true

        paintBackground = Paint()
        paintBackground!!.isAntiAlias = true

        // Load the styled attributes and set their properties
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView, defStyleAttr, 0)

        // Init Border
        if (attributes.getBoolean(R.styleable.CircularImageView_civ_border, true)) {
            val defaultBorderSize = DEFAULT_BORDER_WIDTH * getContext().resources.displayMetrics.density
            setBorderWidth(attributes.getDimension(R.styleable.CircularImageView_civ_border_width, defaultBorderSize))
            setBorderColor(attributes.getColor(R.styleable.CircularImageView_civ_border_color, Color.WHITE))
        }

        setBackgroundColor(attributes.getColor(R.styleable.CircularImageView_civ_background_color, Color.WHITE))

        // Init Shadow
        if (attributes.getBoolean(R.styleable.CircularImageView_civ_shadow, false)) {
            shadowRadius = DEFAULT_SHADOW_RADIUS
            drawShadow(attributes.getFloat(R.styleable.CircularImageView_civ_shadow_radius, shadowRadius),
                    attributes.getColor(R.styleable.CircularImageView_civ_shadow_color, shadowColor))
            val shadowGravityIntValue = attributes.getInteger(R.styleable.CircularImageView_civ_shadow_gravity, ShadowGravity.BOTTOM.value)
            shadowGravity = ShadowGravity.fromValue(shadowGravityIntValue)
        }

        radius = attributes.getDimension(R.styleable.CircularImageView_civ_radius, 0f)

        attributes.recycle()
    }
    //endregion

    //region Set Attr Method
    fun setBorderWidth(borderWidth: Float) {
        this.borderWidth = borderWidth
        requestLayout()
        invalidate()
    }

    fun setBorderColor(borderColor: Int) {
        if (paintBorder != null)
            paintBorder!!.setColor(borderColor)
        invalidate()
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        if (paintBackground != null)
            paintBackground!!.setColor(backgroundColor)
        invalidate()
    }

    fun addShadow() {
        if (shadowRadius == 0f)
            shadowRadius = DEFAULT_SHADOW_RADIUS
        drawShadow(shadowRadius, shadowColor)
        invalidate()
    }

    fun setShadowRadius(shadowRadius: Float) {
        drawShadow(shadowRadius, shadowColor)
        invalidate()
    }

    fun setShadowColor(shadowColor: Int) {
        drawShadow(shadowRadius, shadowColor)
        invalidate()
    }

    fun setShadowGravity(shadowGravity: ShadowGravity) {
        this.shadowGravity = shadowGravity
        invalidate()
    }

    override fun setColorFilter(colorFilter: ColorFilter) {
        if (this.myColorFilter === colorFilter)
            return
        this.myColorFilter = colorFilter
        myDrawable = null // To force re-update shader
        invalidate()
    }

    override fun getScaleType(): ScaleType {
        val currentScaleType = super.getScaleType()
        return if (currentScaleType == null || currentScaleType != ScaleType.CENTER_INSIDE) ScaleType.CENTER_CROP else currentScaleType
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType != ScaleType.CENTER_CROP && scaleType != ScaleType.CENTER_INSIDE) {
            throw IllegalArgumentException(String.format("ScaleType %s not supported. " + "Just ScaleType.CENTER_CROP & ScaleType.CENTER_INSIDE are available for this library.", scaleType))
        } else {
            super.setScaleType(scaleType)
        }
    }

    //region Draw Method
    override fun onDraw(canvas: Canvas) {
        // Load the bitmap
        loadBitmap()

        // Check if image isn't null
        if (image == null)
            return

        if (!isInEditMode) {
            canvasSize = Math.min(canvas.width, canvas.height)
        }

        // circleCenter is the x or y of the view's center
        // radius is the radius in pixels of the cirle to be drawn
        // paint contains the shader that will texture the shape
        val circleCenter = (canvasSize - borderWidth * 2).toInt() / 2
        val margeWithShadowRadius = shadowRadius * 2

        // Draw Border
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter + borderWidth - margeWithShadowRadius, paintBorder)
        // Draw Circle background
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter - margeWithShadowRadius, paintBackground)
        // Draw CircularImageView
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter - margeWithShadowRadius, paint)
    }

    private fun loadBitmap() {
        if (myDrawable === getDrawable())
            return

        myDrawable = getDrawable()
        image = drawableToBitmap(myDrawable)
        updateShader()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasSize = Math.min(w, h)
        if (image != null)
            updateShader()
    }

    private fun drawShadow(shadowRadius: Float, shadowColor: Int) {
        this.shadowRadius = shadowRadius
        this.shadowColor = shadowColor
        setLayerType(View.LAYER_TYPE_SOFTWARE, paintBorder)

        var dx = 0.0f
        var dy = 0.0f

        when (shadowGravity) {
            ShadowGravity.CENTER -> {
                dx = 0.0f
                dy = 0.0f
            }
            ShadowGravity.TOP -> {
                dx = 0.0f
                dy = -shadowRadius / 2
            }
            ShadowGravity.BOTTOM -> {
                dx = 0.0f
                dy = shadowRadius / 2
            }
            ShadowGravity.START -> {
                dx = -shadowRadius / 2
                dy = 0.0f
            }
            ShadowGravity.END -> {
                dx = shadowRadius / 2
                dy = 0.0f
            }
        }

        paintBorder!!.setShadowLayer(shadowRadius, dx, dy, shadowColor)
    }

    private fun updateShader() {
        if (image == null)
            return

        // Create Shader
        val shader = BitmapShader(image!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        // Center Image in Shader
        var scale = 0f
        var dx = 0f
        var dy = 0f

        when (scaleType) {
            ImageView.ScaleType.CENTER_CROP -> if (image!!.width * height > width * image!!.height) {
                scale = height / image!!.height.toFloat()
                dx = (width - image!!.width * scale) * 0.5f
            } else {
                scale = width / image!!.width.toFloat()
                dy = (height - image!!.height * scale) * 0.5f
            }
            ImageView.ScaleType.CENTER_INSIDE -> if (image!!.width * height < width * image!!.height) {
                scale = height / image!!.height.toFloat()
                dx = (width - image!!.width * scale) * 0.5f
            } else {
                scale = width / image!!.width.toFloat()
                dy = (height - image!!.height * scale) * 0.5f
            }
        }

        val matrix = Matrix()
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)
        shader.setLocalMatrix(matrix)

        // Set Shader in Paint
        paint!!.setShader(shader)

        // Apply myColorFilter
        paint!!.setColorFilter(myColorFilter)
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        } else if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        try {
            // Create Bitmap object out of the myDrawable
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
            drawable.draw(canvas)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    //region Measure Method
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun measureWidth(measureSpec: Int): Int {
        val result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize
        } else {
            // The parent has not imposed any constraint on the child.
            result = canvasSize
        }

        return result
    }

    private fun measureHeight(measureSpecHeight: Int): Int {
        val result: Int
        val specMode = MeasureSpec.getMode(measureSpecHeight)
        val specSize = MeasureSpec.getSize(measureSpecHeight)

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = canvasSize
        }

        return result + 2
    }
}