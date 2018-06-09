package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView

class SectionIndexListView: ListView {

    val index = HashMap<Char, Int>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setAdapter(adapter: ListAdapter?) {
        super.setAdapter(adapter)

    }


}