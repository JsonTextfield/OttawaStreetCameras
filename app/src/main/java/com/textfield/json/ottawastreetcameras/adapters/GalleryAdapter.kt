package com.textfield.json.ottawastreetcameras.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.android.volley.toolbox.ImageRequest
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsRequestQueue
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class GalleryAdapter(private val _context: Context, private val list: List<Camera>) :
    ArrayAdapter<Camera>(_context, 0, list) {

    private var images = HashMap<String, Bitmap>()

    override fun addAll(collection: MutableCollection<out Camera>) {
        super.addAll(collection)
        for (camera in list) {
            val request = ImageRequest(camera.getUrl(), { response ->
                if (response != null) {
                    images[camera.getName()] = response
                }
                Log.w("GALLERY", "Loaded ${camera.getName()}")
            }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, {
                it.printStackTrace()
            })
            request.tag = "Gallery"
            CoroutineScope(Dispatchers.IO).launch {
                StreetCamsRequestQueue.getInstance(_context).add(request)
            }
        }
    }

    private var displayedCameras = list

    private class ViewHolder {
        var card: CardView? = null
        var title: TextView? = null
        var image: ImageView? = null
        var star: ImageView? = null
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Camera {
        return displayedCameras[position]
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder
        val camera = getItem(position)
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(_context).inflate(R.layout.gallery_item, parent, false)
            viewHolder.card = convertView.findViewById(R.id.gallery_item_card)
            viewHolder.title = convertView.findViewById(R.id.label)
            viewHolder.image = convertView.findViewById(R.id.source)
            viewHolder.star = convertView.findViewById(R.id.favourite_icon)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = (context as Activity).windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }

        viewHolder.title?.text = camera.getName()
        viewHolder.star?.visibility = if (camera.isFavourite) View.VISIBLE else View.INVISIBLE

        viewHolder.image?.setImageBitmap(images[camera.getName()])

        // Return the completed view to render on screen
        return convertView!!
    }
}