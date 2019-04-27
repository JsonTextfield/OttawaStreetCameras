package com.textfield.json.ottawastreetcameras.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsRequestQueue
import com.textfield.json.ottawastreetcameras.adapters.ImageAdapter
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : GenericActivity() {
    private var cameras = ArrayList<Camera>()
    private var timers = ArrayList<CameraRunnable>()
    private val handler = Handler()
    private val tag = "camera"
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameras = intent.getParcelableArrayListExtra<Camera>("cameras")
        imageAdapter = ImageAdapter(this, cameras)
        image_listView.adapter = imageAdapter
        getSessionId()
    }

    private fun stop() {
        StreetCamsRequestQueue.getInstance(this).cancelAll(tag)
        for (timer in timers) {
            handler.removeCallbacks(timer)
        }
    }

    override fun onResume() {
        getSessionId()
        super.onResume()
    }

    override fun onPause() {
        stop()
        super.onPause()
    }

    override fun onDestroy() {
        stop()
        super.onDestroy()
    }

    private inner class CameraRunnable(index: Int) : Runnable {
        val i = index
        override fun run() {
            download(i)
            handler.postDelayed(this, 6000L)
        }
    }

    fun back(v: View) {
        val intent = Intent()
        intent.putParcelableArrayListExtra("cameras", cameras)
        setResult(0, intent)
        finish()
    }

    fun download(index: Int) {
        val bmImage = image_listView.getViewByPosition(index).findViewById(R.id.source) as ImageView
        val url = "https://traffic.ottawa.ca/map/camera?id=${imageAdapter.getItem(index).num}"

        val request = ImageRequest(url, Response.Listener<Bitmap> { response ->
            camera_progress_bar.visibility = View.INVISIBLE
            try {
                bmImage.setImageResource(android.R.color.transparent)
                bmImage.setImageBitmap(response)
            } catch (e: NullPointerException) {
            }
        }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, Response.ErrorListener {
            it.printStackTrace()
        })

        request.tag = tag
        StreetCamsRequestQueue.getInstance(this).add(request)
    }

    private fun getSessionId() {
        val url = "https://traffic.ottawa.ca/map/"
        val sessionRequest = object : StringRequest(url, Response.Listener {
            for (i in 0 until imageAdapter.count) {
                val cameraRunnable = CameraRunnable(i)
                timers.add(cameraRunnable)
                cameraRunnable.run()
            }
        }, Response.ErrorListener {
            it.printStackTrace()
            showErrorDialogue(this)
        }) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                val sessionId = response.headers["Set-Cookie"] ?: StreetCamsRequestQueue.sessionId
                StreetCamsRequestQueue.sessionId = sessionId
                return Response.success(StreetCamsRequestQueue.sessionId, HttpHeaderParser.parseCacheHeaders(response))
            }
        }
        sessionRequest.tag = tag
        StreetCamsRequestQueue.getInstance(this).add(sessionRequest)
    }
}

//https://stackoverflow.com/questions/24811536/android-listview-get-item-view-by-position/24864536
fun ListView.getViewByPosition(pos: Int): View {
    val lastListItemPosition = firstVisiblePosition + childCount - 1
    return if (pos < firstVisiblePosition || pos > lastListItemPosition) {
        adapter.getView(pos, null, this)
    } else {
        getChildAt(pos - firstVisiblePosition)
    }
}


