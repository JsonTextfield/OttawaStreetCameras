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
import com.textfield.json.ottawastreetcameras.Camera
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.activities.AlternateMainActivity
import com.textfield.json.ottawastreetcameras.activities.addRemoveFavourites
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Created by Jason on 24/10/2017.
 */

class CameraAdapter(private val _context: Context, list: ArrayList<Camera>) : ArrayAdapter<Camera>(_context, 0, list) {
    private var cameras = list
    private val wholeCameras = list

    private class ViewHolder {
        internal var title: TextView? = null
        internal var star: ImageView? = null
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
        val camera = getItem(position)!!
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(_context).inflate(R.layout.list_item, parent, false)
            viewHolder.title = convertView!!.findViewById<TextView>(R.id.listtitle)
            viewHolder.star = convertView.findViewById<ImageView>(R.id.star)
            convertView.tag = viewHolder

        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.title!!.text = camera.getName()
        viewHolder.star!!.setImageDrawable(if (camera.isFavourite) {
            ContextCompat.getDrawable(context, R.drawable.outline_star_white_18)
        } else {
            ContextCompat.getDrawable(context, R.drawable.outline_star_border_white_18)
        })
        viewHolder.star!!.setOnClickListener {
            (context as AlternateMainActivity).addRemoveFavourites(ArrayList<Camera>(Arrays.asList(camera)), !camera.isFavourite)
            camera.isFavourite = !camera.isFavourite
            viewHolder.star!!.setImageDrawable(ContextCompat.getDrawable(context,
                    if (camera.isFavourite) R.drawable.outline_star_white_18 else R.drawable.outline_star_border_white_18))
            (context as AlternateMainActivity).refreshMarkers()
            notifyDataSetChanged()
        }

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
                val sharedPrefs = context.getSharedPreferences(context.applicationContext.packageName, Context.MODE_PRIVATE)

                val filteredResults = when {
                    constraint.startsWith("f: ") -> wholeCameras.filter {
                        it.getName().contains(constraint.removePrefix("f: "), true) &&
                                sharedPrefs.getStringSet("favourites", HashSet<String>()).contains(it.num.toString())
                    }
                    constraint.startsWith("n: ") -> wholeCameras.filter {
                        it.neighbourhood.contains(constraint.removePrefix("n: "), true)
                    }
                    else -> wholeCameras.filter {
                        it.getName().contains(constraint, true)
                    }
                }

                val results = Filter.FilterResults()
                results.values = filteredResults

                return results
            }
        }
    }
}
