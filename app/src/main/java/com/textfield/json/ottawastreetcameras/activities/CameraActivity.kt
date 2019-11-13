package com.textfield.json.ottawastreetcameras.activities

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.material.snackbar.Snackbar
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsRequestQueue
import com.textfield.json.ottawastreetcameras.adapters.ImageAdapter
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class CameraActivity : GenericActivity() {
    private val requestForSave = 0
    private var timers = ArrayList<CameraRunnable>()
    private val handler = Handler()
    private val tag = "camera"
    private lateinit var imageAdapter: ImageAdapter
    private var shuffle = false
    private var workaround = true

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if (isNightModeOn()) R.style.AppTheme else R.style.AppTheme_Light)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        arrayOf(image_listView, camera_activity_layout).forEach {
            it.setBackgroundColor(if (isNightModeOn()) Color.BLACK else Color.WHITE)
        }
        listView = image_listView
        shuffle = intent.getBooleanExtra("shuffle", false)
        cameras = intent.getParcelableArrayListExtra<Camera>("cameras")
        imageAdapter = ImageAdapter(this, if (shuffle) cameras.subList(0, 1) else cameras)
        listView.adapter = imageAdapter
        listView.setMultiChoiceModeListener(this)
        getSessionId()
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelableArrayList<Camera>("selectedCameras") != null) {
                val cameras = savedInstanceState.getParcelableArrayList<Camera>("selectedCameras")!!
                startActionMode(this)
                (0 until imageAdapter.count).forEach {
                    if (imageAdapter.getItem(it) in cameras) {
                        listView.setItemChecked(it, true)
                    }
                }
            }
        }
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        if (!shuffle) {
            super.onCreateActionMode(mode, menu)
            showCameras.isVisible = false
            return true
        }
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (!super.onActionItemClicked(mode, item)) {
            return when (item.itemId) {
                R.id.save -> {
                    for (i in 0 until cameras.size) {
                        if (cameras[i] in selectedCameras) {
                            val imageDrawable = listView.getViewByPosition(i).findViewById<ImageView>(R.id.source).drawable
                            val title = listView.getViewByPosition(i).findViewById<TextView>(R.id.label)
                            if (imageDrawable != null) {
                                saveToInternalStorage((imageDrawable as BitmapDrawable).bitmap, title.text.toString())
                            }
                        }
                    }
                    true
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
            if (shuffle) {
                shuffleDownload()
            } else {
                download(i)
            }
            handler.postDelayed(this, 6000L)
        }
    }

    fun back(v: View) {
        setResult(0)
        finish()
    }

    fun shuffleDownload() {
        val camera = cameras[Random().nextInt(cameras.size)]
        val url = "https://traffic.ottawa.ca/map/camera?id=${camera.num}"
        val request = ImageRequest(url, Response.Listener<Bitmap> { response ->
            camera_progress_bar.visibility = View.INVISIBLE
            if (workaround) {
                try {
                    val bmImage = image_listView.getViewByPosition(0).findViewById(R.id.source) as ImageView
                    val textView = image_listView.getViewByPosition(0).findViewById(R.id.label) as TextView
                    bmImage.setImageResource(android.R.color.transparent)
                    bmImage.setImageBitmap(response)
                    textView.text = camera.getName()
                } catch (e: NullPointerException) {
                }
            }
            workaround = !workaround
        }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, Response.ErrorListener {
            it.printStackTrace()
        })
        request.tag = tag
        StreetCamsRequestQueue.getInstance(this).add(request)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            if (requestCode == requestForSave) {
                for (i in 0 until cameras.size) {
                    if (cameras[i] in selectedCameras) {
                        val imageDrawable = listView.getViewByPosition(i).findViewById<ImageView>(R.id.source).drawable
                        val title = listView.getViewByPosition(i).findViewById<TextView>(R.id.label)
                        if (imageDrawable != null) {
                            saveToInternalStorage((imageDrawable as BitmapDrawable).bitmap, title.text.toString())
                        }
                    }
                }
            }
        }
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap, fileName: String) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), requestForSave)
        }
        val streetCamsDirectory = File(Environment.getExternalStorageDirectory(), "/Ottawa StreetCams")
        if (!streetCamsDirectory.exists()) {
            streetCamsDirectory.mkdirs()
        }
        val imageFile = File(streetCamsDirectory, fileName.replace(" ", "_")
                + "_" + Date().toString().replace(" ", "_") + ".jpg")
        try {
            val out = FileOutputStream(imageFile)
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()

            val s = Snackbar.make(this.listView,
                    resources.getString(R.string.image_saved_at, imageFile.toString()),
                    Snackbar.LENGTH_LONG)
            s.show()
            //Toast.makeText(this, "Image saved at: $imageFile", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}