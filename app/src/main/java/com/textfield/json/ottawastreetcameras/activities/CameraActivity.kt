package com.textfield.json.ottawastreetcameras.activities

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.material.snackbar.Snackbar
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsRequestQueue
import com.textfield.json.ottawastreetcameras.adapters.ImageAdapter
import com.textfield.json.ottawastreetcameras.databinding.ActivityCameraBinding
import com.textfield.json.ottawastreetcameras.entities.Camera
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class CameraActivity : GenericActivity() {
    private val requestForSave = 0
    private val timers = ArrayList<CameraRunnable>()
    private val handler = Handler(Looper.getMainLooper())
    private val tag = "camera"
    private lateinit var binding: ActivityCameraBinding
    private var shuffle = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listView = binding.imageListView
        shuffle = intent.getBooleanExtra("shuffle", false)
        cameras = intent.getParcelableArrayListExtra("cameras") ?: ArrayList()
        adapter = ImageAdapter(this, if (shuffle) cameras.subList(0, 1) else cameras)
        listView.adapter = adapter
        listView.setMultiChoiceModeListener(this)
        if (savedInstanceState?.getParcelableArrayList<Camera>("selectedCameras") != null) {
            previouslySelectedCameras = savedInstanceState.getParcelableArrayList("selectedCameras")!!
            startActionMode(this)
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

    override fun onResume() {
        super.onResume()

        val url = "https://traffic.ottawa.ca/beta/"
        val sessionRequest = object : StringRequest(url, Response.Listener {
            for (i in 0 until adapter.count) {
                val cameraRunnable = CameraRunnable(i)
                timers.add(cameraRunnable)
                handler.post(cameraRunnable)
            }
        }, Response.ErrorListener {
            it.printStackTrace()
            showErrorDialogue(this)
        }) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                val sessionId = response.headers?.get("Set-Cookie") ?: StreetCamsRequestQueue.sessionId
                StreetCamsRequestQueue.sessionId = sessionId
                return Response.success(StreetCamsRequestQueue.sessionId, HttpHeaderParser.parseCacheHeaders(response))
            }
        }
        sessionRequest.tag = tag
        StreetCamsRequestQueue.getInstance(this).add(sessionRequest)
    }

    override fun onPause() {
        StreetCamsRequestQueue.getInstance(this).cancelAll(tag)
        timers.forEach { handler.removeCallbacks(it) }
        super.onPause()
    }

    private inner class CameraRunnable(val index: Int) : Runnable {
        override fun run() {
            download(index)
            handler.postDelayed(this, 6000L)
        }
    }

    fun back(v: View) {
        setResult(0)
        finish()
    }

    fun download(index: Int) {
        val selectedCamera = if (shuffle) cameras[Random().nextInt(cameras.size)] else adapter.getItem(index)!!
        val bmImage = binding.imageListView.getViewByPosition(index).findViewById(R.id.source) as ImageView
        val textView = binding.imageListView.getViewByPosition(index).findViewById(R.id.label) as TextView
        val url = "https://traffic.ottawa.ca/beta/camera?id=${selectedCamera.num}&timems=${Date().time}"
        val request = ImageRequest(url, { response ->
            try {
                bmImage.setImageBitmap(response)
                textView.text = selectedCamera.getName()
                textView.visibility = View.VISIBLE
            } catch (e: NullPointerException) {
            } finally {
                binding.cameraProgressBar.visibility = View.INVISIBLE
            }
        }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, {
            it.printStackTrace()
        })
        request.tag = tag
        StreetCamsRequestQueue.getInstance(this).add(request)
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
        val streetCamsDirectory = File(getExternalFilesDir(null), "/Ottawa StreetCams")
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