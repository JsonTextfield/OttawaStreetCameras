package com.textfield.json.ottawastreetcameras


import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout


//https://github.com/dongjunkun/IndexStickHeaderListView/blob/master/app/src/main/java/com/yyy/djk/stickfilterindexlistview/SideBar.java
class SectionIndex : View {

    private var onTouchingLetterChangedListener: OnTouchingLetterChangedListener? = null

    val letters = ArrayList<String>()

    private var choose = -1
    private val paint = Paint()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!letters.isEmpty()) {
            val width = width
            val singleHeight = (height / letters.size)

            for (i in letters.indices) {
                paint.color = Color.WHITE
                //paint.typeface = Typeface.DEFAULT
                paint.isAntiAlias = true
                paint.textSize = spToPx(10).toFloat()
                if (i == choose) {
                    paint.color = ContextCompat.getColor(context, R.color.colorAccent)
                    paint.isFakeBoldText = true
                }
                val xPos = width / 2 - paint.measureText(letters[i]) / 2
                val yPos = (1.0f * (i) * singleHeight)
                canvas.drawText(letters[i], xPos, yPos+singleHeight/2, paint)
                paint.reset()
            }
        }
    }

    fun addLetters(arrayList: Collection<String>) {
        letters.clear()
        letters.addAll(arrayList)
        invalidate()
    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        val action = event.action
        val y = event.y
        val oldChoose = choose

        val listener = onTouchingLetterChangedListener

        val c = (y / height * letters.size).toInt()

        when (action) {

            MotionEvent.ACTION_UP -> {

                setBackgroundColor(Color.TRANSPARENT)

                choose = -1
                invalidate()

            }

            else -> {
                setBackgroundResource(R.drawable.sidebar_bg)

                if (oldChoose != c) {
                    if (c in letters.indices) {

                        listener?.onTouchingLetterChanged(letters[c])

                        choose = c
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    fun setOnTouchingLetterChangedListener(onTouchingLetterChangedListener: OnTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener
    }

    interface OnTouchingLetterChangedListener {
        fun onTouchingLetterChanged(s: String)
    }

    fun spToPx(px: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, px.toFloat(), resources.displayMetrics).toInt()
    }

}