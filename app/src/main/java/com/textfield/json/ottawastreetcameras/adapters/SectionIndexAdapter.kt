package com.textfield.json.ottawastreetcameras.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.textfield.json.ottawastreetcameras.R
import java.util.*

class SectionIndexAdapter(context: Context, list: ArrayList<String>) : ArrayAdapter<String>(context, 0, list) {

    private class ViewHolder {
        internal var title: TextView? = null
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder
        val title = getItem(position)
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(context).inflate(R.layout.section_list_item, parent, false)
            viewHolder.title = convertView!!.findViewById<TextView>(R.id.listtitle)
            convertView.tag = viewHolder

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.title?.text = title


        // Return the completed view to render on screen
        return convertView
    }
}