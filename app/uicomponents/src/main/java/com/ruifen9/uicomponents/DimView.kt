package com.ruifen9.uicomponents

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.abs


class DimView : View, GestureDetector.OnGestureListener {

    lateinit var mDetector: GestureDetector
    private var orientation = Orientation.HORIZONTAL
    private var progress = 0.5f
    private val progressPaint = Paint()
    private val scalePaint = Paint()
    private val textPaint = TextPaint()
    var radius = 50f

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        mDetector = GestureDetector(context, this)

        textPaint.flags = Paint.ANTI_ALIAS_FLAG
        textPaint.textAlign = Paint.Align.LEFT
        progressPaint.style = Paint.Style.FILL
        scalePaint.color = Color.DKGRAY

        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.DimView, defStyle, 0
        )
        radius = a.getDimension(R.styleable.DimView_dim_radius, 50f)
        val backgroundColorStart =
            a.getColor(R.styleable.DimView_dim_background_color_start, Color.LTGRAY)
        val backgroundColorEnd =
            a.getColor(R.styleable.DimView_dim_background_color_end, Color.LTGRAY)
        val foregroundColorStart =
            a.getColor(R.styleable.DimView_dim_foreground_color_start, Color.GRAY)
        val foregroundColorEnd =
            a.getColor(R.styleable.DimView_dim_foreground_color_end, Color.GRAY)
        backgroundColors[0] = backgroundColorStart
        backgroundColors[1] = backgroundColorEnd
        foregroundColors[0] = foregroundColorStart
        foregroundColors[1] = foregroundColorEnd

        showProgressText = a.getBoolean(R.styleable.DimView_dim_show_progress, true)

        textPaint.textSize = a.getDimension(R.styleable.DimView_dim_progress_textSize, 25f)
        textPaint.color = a.getColor(R.styleable.DimView_dim_progress_textColor, Color.YELLOW)

        if (a.hasValue(R.styleable.DimView_dim_icon)) {
            icon = a.getDrawable(
                R.styleable.DimView_dim_icon
            )
            icon?.callback = this

            iconSize = a.getDimension(R.styleable.DimView_dim_iconSize, 48f)
        }

        orientation = if (a.getInt(R.styleable.DimView_dim_orientation, 0) == 0) {
            Orientation.HORIZONTAL
        } else {
            Orientation.VERTICAL
        }


        a.recycle()


    }

    private var icon: Drawable? = null
    var iconSize = 48f


    private val backgroundColors = IntArray(2)
    private var backgroundLinearGradient: LinearGradient? = null
    private fun setBackgroundColor(startColor: Int, endColor: Int) {
        backgroundColors[0] = startColor
        backgroundColors[1] = endColor
        backgroundLinearGradient = LinearGradient(
            0f,
            0f,
            0f,
            height.toFloat(),
            backgroundColors,
            floatArrayOf(0.25f, 1f),
            Shader.TileMode.MIRROR
        )
        invalidate()
    }

    private val foregroundColors = IntArray(2)
    private var foregroundLinearGradient: LinearGradient? = null
    private fun setForegroundColor(startColor: Int, endColor: Int) {
        foregroundColors[0] = startColor
        foregroundColors[1] = endColor
        foregroundLinearGradient = LinearGradient(
            0f,
            0f,
            0f,
            height.toFloat(),
            foregroundColors,
            floatArrayOf(0.25f, 1f),
            Shader.TileMode.MIRROR
        )
        invalidate()
    }

    fun setProgressTextSize(sp: Float) {
        textPaint.textSize = sp
        invalidate()
    }

    fun setProgressTextColor(color: Int) {
        textPaint.color = color
    }

    var showProgressText = true
    fun enableProgressText(enable: Boolean) {
        showProgressText = enable
        invalidate()
    }

    fun setDimRadius(radius: Float) {
        this.radius = radius
        invalidate()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calRect()
        setForegroundColor(foregroundColors[0], foregroundColors[1])
        setBackgroundColor(backgroundColors[0], backgroundColors[1])
    }

    private fun getTextStartPoint(str: String): PointF {
        val textHalfWidth = textPaint.measureText(str) * 0.5f
        //计算baseline
        val fontMetrics = textPaint.fontMetrics
        val textHalfHeight = (fontMetrics.bottom - fontMetrics.top) * 0.5f

        return if (orientation == Orientation.HORIZONTAL) {
            val offset = height / 2
            PointF(width - textHalfWidth * 2 - offset, height * 0.5f + textHalfHeight * 0.5f)
        } else {
            val offset = width * 0.5f
            PointF(width / 2 - textHalfWidth, offset)
        }
    }

    private var progressRectF = RectF()
    private var backgroundRectF = RectF()
    private var rectF = RectF()
    private fun calRect() {
        if (orientation == Orientation.HORIZONTAL) {
            val offset = width * progress
            progressRectF.set(0f, 0f, offset, height.toFloat())
            backgroundRectF.set(offset, 0f, width.toFloat(), height.toFloat())
        } else {
            val offset = height * (1 - progress)
            progressRectF.set(0f, offset, width.toFloat(), height.toFloat())
            backgroundRectF.set(0f, 0f, width.toFloat(), offset)
        }
        rectF.set(
            0.toFloat(),
            0.toFloat(),
            width.toFloat(),
            height.toFloat()
        )
        invalidate()
    }

    private var xfermode2: PorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.scale(scale, scale, width * 0.5f, height * 0.5f)
        drawProgress(canvas)
        drawScale(canvas)
        drawProgressText(canvas)
        drawIcon(canvas)
    }

    private fun drawProgress(canvas: Canvas) {
        /**
         * 设置View的离屏缓冲。在绘图的时候新建一个“层”，所有的操作都在该层而不会影响该层以外的图像
         * 必须设置，否则设置的PorterDuffXfermode会无效，具体原因不明
         */
        val saveId = canvas.saveLayer(rectF, progressPaint) //必须
        //draw dst
        canvas.drawRoundRect(rectF, radius, radius, progressPaint)
        progressPaint.xfermode = xfermode2
        progressPaint.color = Color.LTGRAY
        //draw src
        progressPaint.shader = backgroundLinearGradient
        canvas.drawRect(backgroundRectF, progressPaint)
        progressPaint.shader = foregroundLinearGradient
        canvas.drawRect(progressRectF, progressPaint)
        progressPaint.xfermode = null;
        canvas.restoreToCount(saveId);//必须
    }

    private val longScaleFlag = 10
    private val shortScaleFlag = 2

    /**
     * 刻度
     */
    private fun drawScale(canvas: Canvas) {
        if (orientation == Orientation.HORIZONTAL) {
            val offset = width * 0.01
            val maxSize = height * 0.33
            val minSize = maxSize * 0.5
            val cy = height * 0.5
            for (index in 1..99) {
                val x = (index * offset).toFloat()

                scalePaint.alpha = ((1 - abs(50 - index).toFloat() / 50) * 255).toInt()
                if (index % longScaleFlag == 0) {
                    val yStart = (cy - maxSize * 0.5).toFloat()
                    val yEnd = (cy + maxSize * 0.5).toFloat()
                    //长刻度
                    canvas.drawLine(x, yStart, x, yEnd, scalePaint)
                } else if (index % shortScaleFlag == 0) {
                    //短刻度
                    val yStart = (cy - minSize * 0.5).toFloat()
                    val yEnd = (cy + minSize * 0.5).toFloat()
                    canvas.drawLine(x, yStart, x, yEnd, scalePaint)
                }
            }
        } else {
            val offset = height * 0.01
            val maxSize = width * 0.33
            val minSize = maxSize * 0.5
            val cx = width * 0.5
            for (index in 1..99) {
                scalePaint.alpha = ((1 - abs(50 - index).toFloat() / 50) * 255).toInt()
                val y = (index * offset).toFloat()
                if (index % 5 == 0) {
                    val xStart = (cx - maxSize * 0.5).toFloat()
                    val xEnd = (cx + maxSize * 0.5).toFloat()
                    //长刻度
                    canvas.drawLine(xStart, y, xEnd, y, scalePaint)
                } else {
                    //短刻度
                    val xStart = (cx - minSize * 0.5).toFloat()
                    val xEnd = (cx + minSize * 0.5).toFloat()
                    canvas.drawLine(xStart, y, xEnd, y, scalePaint)
                }
            }
        }
    }

    private fun drawProgressText(canvas: Canvas) {
        val progressStr = "${(progress * 100).toInt()}%"
        val pointF = getTextStartPoint((progress * 100).toInt().toString())
        canvas.drawText(progressStr, pointF.x, pointF.y, textPaint)
    }

    private fun drawIcon(canvas: Canvas) {
        val offset = if (orientation == Orientation.HORIZONTAL) {
            height * 0.5f
        } else {
            width * 0.5f
        }

        icon?.let {
            val size = iconSize * 0.5f

            val iconPointF = PointF(offset, offset)

            it.setBounds(
                (iconPointF.x - size).toInt(),
                (iconPointF.y - size).toInt(),
                (iconPointF.x + size).toInt(),
                (iconPointF.y + size).toInt()
            )
            it.draw(canvas)
        }
    }


    enum class Orientation {
        HORIZONTAL, VERTICAL
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    //                                  滑动手势/动画
    /////////////////////////////////////////////////////////////////////////////////////////////
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //即将销毁
        animator?.takeIf { it.isRunning }?.apply { cancel() }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val detectedUp = event!!.action == MotionEvent.ACTION_UP
        if (!mDetector.onTouchEvent(event) && detectedUp) {
            onUp(event)
        }
        return true
    }

    override fun onShowPress(e: MotionEvent?) {
        //短触
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        //一次短按事件
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        startAnimator(scale, 0.98f)
        //按下
        return true
    }

    private fun onUp(event: MotionEvent?) {
        startAnimator(scale, 1f)
    }

    var animator: ValueAnimator? = null
    var scale = 1f
    private fun startAnimator(startScale: Float, endScale: Float) {
        animator?.takeIf { it.isRunning }?.apply { cancel() }
        animator = ValueAnimator.ofFloat(startScale, endScale)
        animator?.apply {
            duration = 300
            repeatCount = 0
            interpolator = AccelerateDecelerateInterpolator()//DecelerateInterpolator()
            addUpdateListener {
                scale = it.animatedValue as Float
                invalidate()
                //改变后的值发赋值给对象的属性值
//            view.setproperty(currentValue)
                //刷新视图
//            view.requestLayout()
            }
        }?.start()
    }

    override fun onFling(
        e1: MotionEvent?,//down事件
        e2: MotionEvent?,//end 事件
        velocityX: Float,//速度
        velocityY: Float//速度
    ): Boolean {
        //一次短时间滑动事件
        startAnimator(scale, 1f)
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,//down事件
        e2: MotionEvent?,//current
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (e1 == null || e2 == null) return false
        progress = if (orientation == Orientation.HORIZONTAL) {
            val location = progress * width - distanceX
            location / width
        } else {
            val location = progress * height + distanceY
            location / height
        }
        if (progress > 1) {
            progress = 1f
        } else if (progress < 0) {
            progress = 0f
        }
        calRect()
        invalidate()
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        //长按但未滑动
    }
}
