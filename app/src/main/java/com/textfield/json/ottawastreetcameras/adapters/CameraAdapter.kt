package com.textfield.json.ottawastreetcameras.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.textfield.json.ottawastreetcameras.adapters.filters.CameraFilter
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.activities.MainActivity
import com.textfield.json.ottawastreetcameras.activities.GenericActivity
import com.textfield.json.ottawastreetcameras.entities.Camera
import java.util.*

/**
 * Created by Jason on 24/10/2017.
 */

abstract class CameraAdapter(private val _context: Context, private val list: List<Camera>) : ArrayAdapter<Camera>(_context, 0, list) {
    private var cameras = list

    private class ViewHolder {
        var title: TextView? = null
        var neighbourhood: TextView? = null
        var star: ImageView? = null
    }

    override fun getItem(position: Int): Camera {
        return cameras[position]
    }

    override fun getCount(): Int {
        return cameras.size
    }

    override fun sort(comparator: Comparator<in Camera>) {
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
            viewHolder.neighbourhood = convertView.findViewById(R.id.neighbourhood)
            viewHolder.star = convertView.findViewById(R.id.star)
            convertView.tag = viewHolder

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.title?.text = camera.getName()
        viewHolder.neighbourhood?.text = camera.neighbourhood
        viewHolder.neighbourhood?.visibility = if (camera.neighbourhood.isEmpty()) View.GONE else View.VISIBLE
        val icon = if (camera.isFavourite) R.drawable.outline_star_white_18 else R.drawable.outline_star_border_white_18
        viewHolder.star?.setImageDrawable(ContextCompat.getDrawable(_context, icon))

        if ((context as GenericActivity).isNightModeOn()) {
            viewHolder.star?.setColorFilter(ContextCompat.getColor(context, android.R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            viewHolder.star?.setColorFilter(ContextCompat.getColor(context, android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
        }
        viewHolder.star?.setOnClickListener {
            (context as MainActivity).modifyPrefs("favourites", listOf(camera), !camera.isFavourite)
            camera.setFavourite(!camera.isFavourite)
            camera.marker
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
