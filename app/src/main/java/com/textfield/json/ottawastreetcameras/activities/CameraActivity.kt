package com.textfield.json.ottawastreetcameras.activities

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class CameraActivity : GenericActivity() {
    private var timers = ArrayList<CameraRunnable>()
    private val handler = Handler()
    private val tag = "camera"
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        listView = image_listView
        cameras = intent.getParcelableArrayListExtra<Camera>("cameras")
        imageAdapter = ImageAdapter(this, cameras)
        listView.adapter = imageAdapter
        listView.setMultiChoiceModeListener(this)
        getSessionId()
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        super.onCreateActionMode(mode, menu)
        showCameras.isVisible = false
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (!super.onActionItemClicked(mode, item)) {
            return when (item.itemId) {
                R.id.save -> {
                    for (i in 0..cameras.size) {
                        if (cameras[i] in selectedCameras) {
                            val imageView = listView.getViewByPosition(i).findViewById<ImageView>(R.id.source)
                            saveToInternalStorage((imageView.drawable as BitmapDrawable).bitmap)
                        }
                    }
                    return true
                }
                else -> false
            }
        }
        return true
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
        setResult(0)
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
    private fun saveToInternalStorage(bitmapImage: Bitmap) {
        val streetCamsDirectory = File(Environment.getExternalStorageDirectory(), "/Ottawa StreetCams/")
        if (!streetCamsDirectory.exists()) {
            streetCamsDirectory.mkdirs()
        }
        val imageFile = File(streetCamsDirectory, Date().time.toString() + ".jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(imageFile)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
            Toast.makeText(this, "Image saved at: $imageFile", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}