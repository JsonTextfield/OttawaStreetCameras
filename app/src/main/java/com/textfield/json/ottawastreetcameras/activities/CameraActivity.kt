package com.textfield.json.ottawastreetcameras.activities

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsRequestQueue
import com.textfield.json.ottawastreetcameras.adapters.ImageAdapter
import com.textfield.json.ottawastreetcameras.databinding.ActivityCameraBinding
import com.textfield.json.ottawastreetcameras.entities.Camera
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class CameraActivity : GenericActivity() {
    private val requestForSave = 0
    private val timers = ArrayList<CameraRunnable>()
    private val handler = Handler(Looper.getMainLooper())
    private val tag = "camera"
    private lateinit var binding: ActivityCameraBinding
    private var shuffle = false

    private inner class CameraRunnable(val index: Int) : Runnable {
        override fun run() {
            download(index)
            handler.postDelayed(this, 6000L)
        }
    }

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
        binding.backButton.setOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        (0 until adapter.count).forEach {
            CameraRunnable(it).apply {
                timers.add(this)
                handler.post(this)
            }
        }
    }

    override fun onPause() {
        StreetCamsRequestQueue.getInstance(this).cancelAll(tag)
        timers.forEach { handler.removeCallbacks(it) }
        super.onPause()
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        if (!shuffle) {
            super.onCreateActionMode(mode, menu)
            showCameras.isVisible = false
            return true
        }
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        (0 until listView.adapter.count).forEach {
            listView.getViewByPosition(it).findViewById<View>(R.id.overlay).visibility = View.INVISIBLE
        }
        super.onDestroyActionMode(mode)
    }

    override fun onItemCheckedStateChanged(mode: ActionMode?, position: Int, id: Long, checked: Boolean) {
        super.onItemCheckedStateChanged(mode, position, id, checked)
        val overlay = listView.getViewByPosition(position).findViewById<View>(R.id.overlay)
        overlay.visibility = if (checked) View.VISIBLE else View.INVISIBLE
        hideMenuOptions()
    }

    private fun hideMenuOptions() {
        unhide.isVisible = false
        hide.isVisible = false
        addFav.isVisible = false
        removeFav.isVisible = false
    }

    private fun requestStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestForSave
            )
            return false
        }
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (!super.onActionItemClicked(mode, item)) {
            return when (item.itemId) {
                R.id.save -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || requestStoragePermission()) {
                        saveSelectedImages()
                    }
                    true
                }
                else -> false
            }
        }
        return true
    }

    fun download(index: Int) {
        val selectedCamera = if (shuffle) cameras[Random().nextInt(cameras.size)] else adapter.getItem(index)!!
        val bmImage = listView.getViewByPosition(index).findViewById<ImageView>(R.id.source)
        val textView = listView.getViewByPosition(index).findViewById<TextView>(R.id.label)
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
            showErrorDialogue(this@CameraActivity)
            StreetCamsRequestQueue.getInstance(this).cancelAll(tag)
            timers.forEach { item -> handler.removeCallbacks(item) }
        })
        request.tag = tag
        StreetCamsRequestQueue.getInstance(this).add(request)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && requestCode == requestForSave
        ) {
            saveSelectedImages()
        }
    }

    private fun saveSelectedImages() {
        (cameras.indices).forEach {
            if (cameras[it] in selectedCameras) {
                val imageDrawable = listView.getViewByPosition(it).findViewById<ImageView>(R.id.source).drawable
                val title = listView.getViewByPosition(it).findViewById<TextView>(R.id.label)
                if (imageDrawable != null) {
                    saveImage((imageDrawable as BitmapDrawable).bitmap, title.text.toString())
                }
            }
        }
    }

    private fun saveImage(bitmapImage: Bitmap, fileName: String) {
        // Add a media item that other apps shouldn't see until the item is fully written to the media store.
        val resolver = applicationContext.contentResolver

        // Find all audio files on the primary external storage device.
        val imageCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, (fileName + "_" + Date().toString() + ".jpg").replace(" ", "_"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val songContentUri = resolver.insert(imageCollection, imageDetails)

        resolver.openFileDescriptor(songContentUri!!, "w", null).use { pfd ->
            // Write data into the pending audio file.

            try {
                val out = FileOutputStream(pfd?.fileDescriptor)
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Now that we're finished, release the "pending" status, and allow other apps to play the audio track.
        imageDetails.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
        }
        resolver.update(songContentUri, imageDetails, null, null)
        Snackbar.make(listView, resources.getString(R.string.image_saved), Snackbar.LENGTH_LONG).show()
    }
}