package com.textfield.json.ottawastreetcameras.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.textfield.json.ottawastreetcameras.Camera
import com.textfield.json.ottawastreetcameras.R

/**
 * Created by Jason on 08/02/2018.
 */
class ImageAdapter(private val _context: Context, list: ArrayList<Camera>) : ArrayAdapter<Camera>(_context, 0, list) {

    private class ViewHolder {
        internal var title: TextView? = null
        internal var image: ImageView? = null
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder

        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(_context).inflate(R.layout.camera_item, parent, false)
            viewHolder.title = convertView.findViewById(R.id.label) as TextView
            viewHolder.image = convertView.findViewById(R.id.source) as ImageView
            convertView.tag = viewHolder

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.title!!.text = getItem(position).getName()

        // Return the completed view to render on screen
        return convertView!!
    }
}