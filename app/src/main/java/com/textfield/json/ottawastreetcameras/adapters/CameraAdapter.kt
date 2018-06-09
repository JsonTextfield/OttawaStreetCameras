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
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.CameraFilter
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.activities.AlternateMainActivity
import com.textfield.json.ottawastreetcameras.activities.modifyPrefs
import kotlinx.android.synthetic.main.activity_alternate_main.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jason on 24/10/2017.
 */

abstract class CameraAdapter(private val _context: Context, list: ArrayList<Camera>) : ArrayAdapter<Camera>(_context, 0, list) {

    private val allCameras = list
    private var cameras = allCameras
    private val index = HashMap<Char, Int>()

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

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder
        val camera = getItem(position)
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(_context).inflate(R.layout.list_item, parent, false)
            viewHolder.title = convertView!!.findViewById<TextView>(R.id.listtitle)
            viewHolder.star = convertView.findViewById<ImageView>(R.id.star)
            convertView.tag = viewHolder

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.title?.text = camera.getName()
        viewHolder.star?.setImageDrawable(if (camera.isFavourite) {
            ContextCompat.getDrawable(context, R.drawable.outline_star_white_18)
        } else {
            ContextCompat.getDrawable(context, R.drawable.outline_star_border_white_18)
        })
        viewHolder.star?.setOnClickListener {
            (context as AlternateMainActivity).modifyPrefs("favourites", Arrays.asList(camera), !camera.isFavourite)
            camera.isFavourite = !camera.isFavourite
            notifyDataSetChanged()
        }

        // Return the completed view to render on screen
        return convertView
    }

    private fun setupSectionIndex() {
        index.clear()
        //assumes cameras are sorted
        for (i in 0 until count) {

            //get the first character
            val c = getItem(i).getSortableName()[0]
            if (c !in index.keys && getItem(i).isVisible) {
                index[c] = i
            }
        }
    }

    override fun getFilter(): Filter {
        return object : CameraFilter(allCameras){
            override fun refresh(list: ArrayList<Camera>) {
                cameras = list
                notifyDataSetChanged()
                complete()
            }
        }
    }

    abstract fun complete()
}
