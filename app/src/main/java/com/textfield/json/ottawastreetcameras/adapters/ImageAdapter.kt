package com.textfield.json.ottawastreetcameras.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

/**
 * Created by Jason on 08/02/2018.
 */
class ImageAdapter(private val _context: Context, list: List<Camera>) :
    ArrayAdapter<Camera>(_context, 0, list) {

    private class ViewHolder {
        var title: TextView? = null
        var image: ImageView? = null
        var layout: RelativeLayout? = null
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
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val camera = getItem(position)
        viewHolder.title?.text = camera?.getName()

        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        val result = if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            30
        }
        val height = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = (context as Activity).windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
        viewHolder.image?.maxHeight = height - result

        // Return the completed view to render on screen
        return convertView!!
    }
}