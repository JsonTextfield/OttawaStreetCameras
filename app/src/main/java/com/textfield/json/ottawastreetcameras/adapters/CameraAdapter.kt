package com.textfield.json.ottawastreetcameras.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.textfield.json.ottawastreetcameras.Camera
import com.textfield.json.ottawastreetcameras.R
import java.util.ArrayList
import java.util.Locale

/**
 * Created by Jason on 24/10/2017.
 */

internal class CameraAdapter(private val _context: Context, list: ArrayList<Camera>) : ArrayAdapter<Camera>(_context, 0, list) {

    var cameras: ArrayList<Camera>
    var wholeCameras: ArrayList<Camera>

    init {
        cameras = list
        wholeCameras = cameras
    }

    private class ViewHolder {
        internal var title: TextView? = null
    }

    override fun getItem(position: Int): Camera? {
        return cameras[position]
    }

    override fun getCount(): Int {
        return cameras.size
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder

        val item = getItem(position)

        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(_context)
            convertView = inflater.inflate(R.layout.list_item, parent, false)
            viewHolder.title = convertView!!.findViewById<View>(R.id.listtitle) as TextView
            convertView.tag = viewHolder

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.title!!.text = if (Locale.getDefault().displayLanguage.contains("fr")) item!!.nameFr
        else item!!.name

        // Return the completed view to render on screen
        return convertView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                cameras = results.values as ArrayList<Camera>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
                val filteredResults = wholeCameras.filter {
                    it.name.toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            it.nameFr.toLowerCase().contains(constraint.toString().toLowerCase())
                }

                val results = Filter.FilterResults()
                results.values = filteredResults

                return results
            }
        }
    }
}
