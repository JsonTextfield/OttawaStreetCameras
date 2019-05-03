package com.textfield.json.ottawastreetcameras.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.textfield.json.ottawastreetcameras.CameraFilter
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.activities.AlternateMainActivity
import com.textfield.json.ottawastreetcameras.entities.Camera
import java.util.*

/**
 * Created by Jason on 24/10/2017.
 */

abstract class CameraAdapter(private val _context: Context, private val list: List<Camera>) : ArrayAdapter<Camera>(_context, 0, list) {
    private var cameras = list

    private class ViewHolder {
        internal var title: TextView? = null
        internal var star: ImageView? = null
    }

    override fun getItem(position: Int): Camera {
        return cameras[position]
    }

    override fun getCount(): Int {
        return cameras.size
    }

    override fun sort(comparator: Comparator<in Camera>?) {
        Collections.sort(cameras, comparator)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder
        val camera = getItem(position)
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(_context).inflate(R.layout.list_item, parent, false)
            viewHolder.title = convertView.findViewById(R.id.listtitle)
            viewHolder.star = convertView.findViewById(R.id.star)
            convertView.tag = viewHolder

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.title?.text = camera.getName()

        val icon = if (camera.isFavourite) R.drawable.outline_star_white_18 else R.drawable.outline_star_border_white_18
        viewHolder.star?.setImageDrawable(ContextCompat.getDrawable(_context, icon))
        viewHolder.star?.setOnClickListener {
            (context as AlternateMainActivity).modifyPrefs("favourites", Arrays.asList(camera), !camera.isFavourite)
            camera.isFavourite = !camera.isFavourite
            notifyDataSetChanged()
        }

        // Return the completed view to render on screen
        return convertView!!
    }

    override fun getFilter(): Filter {
        return object : CameraFilter(list) {
            override fun onPublishResults(list: ArrayList<Camera>) {
                cameras = list
                notifyDataSetChanged()
                onComplete()
            }
        }
    }

    abstract fun onComplete()
}
