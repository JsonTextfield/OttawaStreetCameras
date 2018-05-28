package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView

class MyLinearLayout : LinearLayout {
    private var onLetterTouchListener: OnLetterTouchListener? = null

    private var choose = -1

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        val action = event.action
        val yCoordinates = event.y
        val oldChoose = choose
        val listener = onLetterTouchListener
        var selectedIndex = (yCoordinates / height * childCount).toInt()
        if (selectedIndex < 0) {
            selectedIndex = 0
        }
        when (action) {
            MotionEvent.ACTION_UP -> {
                choose = -1
                for (i in 0 until childCount) {
                    (getChildAt(i) as TextView).setTextColor(Color.WHITE)
                }
            }

            else -> {
                if (oldChoose != selectedIndex) {

                    if (oldChoose > -1) {
                        (getChildAt(oldChoose) as TextView).setTextColor(Color.WHITE)
                    }
                    val t = (getChildAt(selectedIndex) as TextView)
                    t.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    listener?.onLetterTouch(t.text[0])
                    choose = selectedIndex
                }
            }
        }
        return true
    }

    fun setOnTouchingLetterChangedListener(onLetterTouchListener: OnLetterTouchListener) {
        this.onLetterTouchListener = onLetterTouchListener
    }


    interface OnLetterTouchListener {
        fun onLetterTouch(c: Char)
    }
}