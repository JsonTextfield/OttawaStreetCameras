package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.section_index_listview.view.*

class SectionIndexListView : LinearLayout {
    var listview: ListView
        private set
    var sectionindex: LinearLayout
        private set
    private val index = HashMap<Char, Int>()
    private var choose = -1

    constructor(context: Context) : super(context)
    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)
    constructor(context: Context, attributes: AttributeSet, int: Int) : super(context, attributes, int)

    init {
        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mInflater.inflate(R.layout.section_index_listview, this, true)
        sectionindex = sectionIndex
        listview = listView
        sectionIndex.setOnTouchListener { _, event ->
            val action = event.action
            val yCoordinates = event.y
            val oldChoose = choose
            var selectedIndex = (yCoordinates / height * sectionIndex.childCount).toInt()
            if (selectedIndex < 0) {
                selectedIndex = 0
            }
            when (action) {
                MotionEvent.ACTION_UP -> {
                    choose = -1
                    (0 until sectionIndex.childCount).forEach { (sectionIndex.getChildAt(it) as TextView).setTextColor(Color.WHITE) }
                }
                else -> {
                    if (oldChoose != selectedIndex) {

                        if (oldChoose > -1) {
                            (sectionIndex.getChildAt(oldChoose) as TextView).setTextColor(Color.WHITE)
                        }
                        try {
                            val t = (sectionIndex.getChildAt(selectedIndex) as TextView)
                            t.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                            listView.setSelection(index[t.text[0]]!!)
                            choose = selectedIndex
                        } catch (e: Exception) {

                        }
                    }
                }
            }
            true
        }
    }

    fun updateIndex() {
        index.clear()
        sectionIndex.removeAllViews()
        for (i in 0 until listView.adapter.count) {
            //get the first character
            val c = listView.adapter.getItem(i).toString()[0]
            if (c !in index.keys) {
                index[c] = i
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val t = inflater.inflate(R.layout.section_index_title, this, false) as TextView
                t.text = c.toString()
                sectionIndex.addView(t)
                sectionindex.invalidate()
            }
        }
    }
}