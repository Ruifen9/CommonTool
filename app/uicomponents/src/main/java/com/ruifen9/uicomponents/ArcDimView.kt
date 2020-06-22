package com.ruifen9.uicomponents

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator


class ArcDimView : View, GestureDetector.OnGestureListener {

    val centerPointF = PointF()

    var innerRadius = 0f
    val innerPaint = Paint()
    val innerRectF = RectF()


    var outerRadius = 0f
    val outerRectF = RectF()
    val outerPaint = Paint()
    val ratio = 0.618f


    private lateinit var mDetector: GestureDetector


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

        innerPaint.flags = Paint.ANTI_ALIAS_FLAG
        innerPaint.color = Color.WHITE
        outerPaint.flags = Paint.ANTI_ALIAS_FLAG
        outerPaint.color = Color.parseColor("#FFCEB7")

//        // Load attributes
//        val a = context.obtainStyledAttributes(
//            attrs, R.styleable.DimView, defStyle, 0
//        )
//
//
//
//        a.recycle()


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = widthSize / 2
        setMeasuredDimension(widthSize, heightSize)
    }


    var sweepGradient: SweepGradient? = null

    //忽略padding
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        outerRadius = w * 0.8f * 0.5f
        innerRadius = outerRadius * ratio

        centerPointF.x = w * 0.5f
        centerPointF.y = h.toFloat()

        outerRectF.set(
            centerPointF.x - outerRadius,
            centerPointF.y - outerRadius,
            centerPointF.x + outerRadius,
            centerPointF.y + outerRadius
        )
        innerRectF.set(
            centerPointF.x - innerRadius,
            centerPointF.y - innerRadius,
            centerPointF.x + innerRadius,
            centerPointF.y + innerRadius
        )

        val colors= intArrayOf(Color.parseColor("#082DD4"),Color.parseColor("#6FB5FE"),Color.parseColor("#082DD4"))
        val positions= floatArrayOf(0f,0.5f,1f)
        sweepGradient=SweepGradient(centerPointF.x,centerPointF.y,colors,positions)
    }


    private var xfermode2: PorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setLayerType(LAYER_TYPE_SOFTWARE, null)//禁用硬件加速，否则shadowLayer无效
        drawOuterArc(canvas)
        drawInnerArc(canvas)

    }

    private fun drawOuterArc(canvas: Canvas) {

        sweepGradient?.let {
            outerPaint.shader=it
        }
        canvas.drawArc(outerRectF, 180f, 180f, false, outerPaint)
    }

    private fun drawInnerArc(canvas: Canvas) {
        innerPaint.setShadowLayer(innerRadius * 0.3f, 0f, 0f, Color.parseColor("#aa000000"))
        canvas.drawArc(innerRectF, 180f, 180f, false, innerPaint)
    }

    fun drawIndicator(canvas: Canvas){
        
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
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        //长按但未滑动
    }
}
