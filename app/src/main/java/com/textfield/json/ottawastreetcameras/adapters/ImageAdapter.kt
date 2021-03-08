package com.textfield.json.ottawastreetcameras.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

/**
 * Created by Jason on 08/02/2018.
 */
class ImageAdapter(private val _context: Context, private val list: List<Camera>)
    : ArrayAdapter<Camera>(_context, 0, list) {

    private class ViewHolder {
        var title: TextView? = null
        var image: ImageView? = null
        var layout: RelativeLayout? = null
    }

    override fun getItem(position: Int): Camera {
        return list[position]
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder

        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(_context).inflate(R.layout.camera_item, parent, false)
            viewHolder.layout = convertView.findViewById(R.id.camera_item_layout) as RelativeLayout
            viewHolder.title = convertView.findViewById(R.id.label) as TextView
            viewHolder.image = convertView.findViewById(R.id.source) as ImageView
            /*viewHolder.image!!.setOnClickListener {
                Log.d("IMAGEVIEW", "OnClick")
                if (viewHolder.image!!.scaleType == ImageView.ScaleType.FIT_CENTER) {
                    viewHolder.image!!.adjustViewBounds = false
                    viewHolder.image!!.scaleType = ImageView.ScaleType.CENTER_INSIDE
                } else {
                    viewHolder.image!!.adjustViewBounds = true
                    viewHolder.image!!.scaleType = ImageView.ScaleType.FIT_CENTER
                }
            }*/
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val camera = getItem(position)
        viewHolder.title!!.text = camera.getName()

        // Return the completed view to render on screen
        return convertView!!
    }
}