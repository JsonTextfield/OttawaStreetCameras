package com.textfield.json.ottawastreetcameras.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.Filter
import android.widget.TextView
import com.textfield.json.ottawastreetcameras.adapters.filters.NeighbourhoodFilter
import com.textfield.json.ottawastreetcameras.R

abstract class NeighbourhoodAdapter(var context: Context, cursor: Cursor) : CursorAdapter(context, cursor, false) {


    private class ViewHolder {
        var title: TextView? = null
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder
        val neighbourhood = getItem(position)
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            viewHolder.title = convertView!!.findViewById(R.id.listtitle)
            convertView.tag = viewHolder

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        //viewHolder.title?.text = neighbourhood.getName()

        // Return the completed view to render on screen
        return convertView
    }

    abstract fun complete()

    override fun getFilter(): Filter {
        return object : NeighbourhoodFilter() {

        }
    }
}
