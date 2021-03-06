package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min

class SectionIndexListView : LinearLayout {
    lateinit var listView: ListView
        private set
    lateinit var sectionIndex: LinearLayout
        private set
    private var indexLocation = 0
    private val index = HashMap<Char, Int>()
    private var choose = -1
    private var indexTextViewLayout = R.layout.section_index_title
    var selectedTextColour = Color.WHITE
    var defaultTextColour = Color.BLUE

    constructor(context: Context) : super(context) {
        initialise()
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        context.theme.obtainStyledAttributes(
                attributes,
                R.styleable.SectionIndexListView,
                0, 0
        ).apply {
            try {
                defaultTextColour = getColor(R.styleable.SectionIndexListView_default_text_colour, Color.WHITE)
                selectedTextColour = getColor(R.styleable.SectionIndexListView_selected_text_colour, Color.BLUE)
                indexLocation = getInteger(R.styleable.SectionIndexListView_index_location, 0)
            } finally {
                recycle()
            }
        }
        initialise()
    }

    constructor(context: Context, attributes: AttributeSet, int: Int) : super(context, attributes, int) {
        initialise()
    }

    private fun initialise() {
        val mInflater = LayoutInflater.from(context)
        mInflater.inflate(R.layout.section_index_listview, this, true)
        sectionIndex = findViewById(R.id.sectionIndex)
        listView = findViewById(R.id.listView)
        val sectionIndexListviewLayout = findViewById<LinearLayout>(R.id.section_index_listview_layout)
        sectionIndexListviewLayout.removeAllViews()
        if (indexLocation == 0) {
            sectionIndexListviewLayout.addView(sectionIndex)
            sectionIndexListviewLayout.addView(listView)
        } else {
            sectionIndexListviewLayout.addView(listView)
            sectionIndexListviewLayout.addView(sectionIndex)
        }
        sectionIndexListviewLayout.invalidate()
        sectionIndex.setOnTouchListener { _, event ->
            val action = event.action
            val yCoordinates = event.y
            var selectedIndex = (yCoordinates / height * sectionIndex.childCount).toInt()
            selectedIndex = min(max(0, selectedIndex), index.size - 1)
            when (action) {
                MotionEvent.ACTION_UP -> {
                    choose = -1
                    if (sectionIndex.getChildAt(selectedIndex) != null) {
                        (sectionIndex.getChildAt(selectedIndex) as TextView).setTextColor(defaultTextColour)
                    }
                }
                else -> {
                    if (choose != selectedIndex) {
                        if (choose > -1) {
                            (sectionIndex.getChildAt(choose) as TextView).setTextColor(defaultTextColour)
                        }
                        try {
                            val t = (sectionIndex.getChildAt(selectedIndex) as TextView)
                            t.setTextColor(selectedTextColour)
                            listView.setSelection(index[t.text[0]]!!)
                            choose = selectedIndex
                        } catch (e: Exception) {
                            e.printStackTrace()
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
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (i in 0 until listView.adapter.count) {
            //get the first character
            val t = inflater.inflate(indexTextViewLayout, this, false) as TextView
            t.setTextColor(defaultTextColour)
            val c = listView.adapter.getItem(i).toString().toUpperCase(Locale.ENGLISH)[0]
            if (c !in index.keys && c in "ABCDEFGHIJKLMNOPQRESTUVWXYZ") {
                index[c] = i
                t.text = c.toString()
                sectionIndex.addView(t)
                sectionIndex.invalidate()
            } else if (c in "0123456789" && !index.containsKey('#')) {
                index['#'] = 0
                t.text = "#"
                sectionIndex.addView(t)
                sectionIndex.invalidate()
            } else if (c !in "0123456789ABCDEFGHIJKLMNOPQRESTUVWXYZ") {
                index['*'] = i
                t.text = "*"
                sectionIndex.addView(t)
                sectionIndex.invalidate()
                break
            }
        }
    }
}