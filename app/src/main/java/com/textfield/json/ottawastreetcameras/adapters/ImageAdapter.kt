package com.textfield.json.ottawastreetcameras.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.activities.CameraActivity
import com.textfield.json.ottawastreetcameras.activities.modifyPrefs
import java.util.*

/**
 * Created by Jason on 08/02/2018.
 */
class ImageAdapter(private val _context: Context, list: ArrayList<Camera>) : ArrayAdapter<Camera>(_context, 0, list) {

    private class ViewHolder {
        internal var title: TextView? = null
        internal var image: ImageView? = null
        internal var star: CheckBox? = null
        internal var eye: CheckBox? = null
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder

        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(_context).inflate(R.layout.camera_item, parent, false)
            viewHolder.title = convertView.findViewById(R.id.label) as TextView
            viewHolder.image = convertView.findViewById(R.id.source) as ImageView
            viewHolder.star = convertView.findViewById(R.id.star) as CheckBox
            viewHolder.eye = convertView.findViewById(R.id.eye) as CheckBox
            convertView.tag = viewHolder

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val camera = getItem(position)
        viewHolder.title!!.text = camera.getName()

        viewHolder.star?.isChecked = camera.isFavourite
        viewHolder.star?.setOnCheckedChangeListener { buttonView, isChecked ->
            (context as CameraActivity).modifyPrefs("favourites", Arrays.asList(camera), isChecked)
            camera.isFavourite = isChecked
            notifyDataSetChanged()
        }

        viewHolder.eye?.isChecked = !(camera.isVisible)
        viewHolder.eye?.setOnCheckedChangeListener { buttonView, isChecked ->
            (context as CameraActivity).modifyPrefs("hidden", Arrays.asList(camera), !isChecked)
            camera.isVisible = !isChecked
            notifyDataSetChanged()
        }

        // Return the completed view to render on screen
        return convertView!!
    }
}