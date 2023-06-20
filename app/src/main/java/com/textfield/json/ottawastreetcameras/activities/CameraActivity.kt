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
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsRequestQueue
import com.textfield.json.ottawastreetcameras.adapters.ImageAdapter
import com.textfield.json.ottawastreetcameras.databinding.ActivityCameraBinding
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.AppTheme
import com.textfield.json.ottawastreetcameras.ui.CameraActivityContent
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

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


    private fun loadView() {
        setContent {
            AppTheme() {
                CameraActivityContent(cameras, shuffle)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listView = binding.imageListView*/
        shuffle = intent.getBooleanExtra("shuffle", false)

        cameras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("cameras", Camera::class.java) ?: cameras
        } else {
            intent.getParcelableArrayListExtra("cameras") ?: ArrayList()
        }
        // if shuffle is on, show only one camera image at a time
        adapter = ImageAdapter(this, if (shuffle) cameras.subList(0, 1) else cameras)
        /*listView.adapter = adapter
        listView.setMultiChoiceModeListener(this)

        loadPreviouslySelectedCameras(savedInstanceState)
        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }*/
        loadView()
    }

    override fun onResume() {
        super.onResume()
        /*(0 until adapter.count).forEach {
            CameraRunnable(it).apply {
                timers.add(this)
                handler.post(this)
            }
        }*/
    }

    override fun onPause() {
        /*StreetCamsRequestQueue.getInstance(this).cancelAll(tag)
        timers.forEach(handler::removeCallbacks)*/
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
            listView.getViewByPosition(it).findViewById<ImageView>(R.id.source)
                .setColorFilter(android.graphics.Color.TRANSPARENT)
            listView.getViewByPosition(it).findViewById<ImageView>(R.id.background)
                .setColorFilter(android.graphics.Color.TRANSPARENT)
        }
        super.onDestroyActionMode(mode)
    }

    override fun onItemCheckedStateChanged(mode: ActionMode?, position: Int, id: Long, checked: Boolean) {
        super.onItemCheckedStateChanged(mode, position, id, checked)
        val source = listView.getViewByPosition(position).findViewById<ImageView>(R.id.source)
        val overlay = listView.getViewByPosition(position).findViewById<ImageView>(R.id.background)

        if (checked) {
            source.setColorFilter(android.graphics.Color.parseColor("#4F00BCD4"))
            overlay.setColorFilter(android.graphics.Color.parseColor("#4F00BCD4"))
        } else {
            source.setColorFilter(android.graphics.Color.TRANSPARENT)
            overlay.setColorFilter(android.graphics.Color.TRANSPARENT)
        }
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
        val selectedCamera = if (shuffle) cameras.random() else adapter.getItem(index)!!
        val bmImage = listView.getViewByPosition(index).findViewById<ImageView>(R.id.source)
        val background = listView.getViewByPosition(index).findViewById<ImageView>(R.id.background)
        val textView = listView.getViewByPosition(index).findViewById<TextView>(R.id.label)
        val request = ImageRequest(selectedCamera.url, { response ->
            if (response != null) {
                listView.getViewByPosition(index).findViewById<TextView>(R.id.could_not_load_textview).visibility =
                    View.INVISIBLE
                bmImage.setImageBitmap(response)
                Blurry.with(this).from(response).into(background)
                textView.text = selectedCamera.getName()
                textView.visibility = View.VISIBLE
            }
        }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, {
            it.printStackTrace()
            listView.getViewByPosition(index).findViewById<TextView>(R.id.could_not_load_textview).visibility =
                View.VISIBLE
        })
        binding.cameraProgressBar.visibility = View.INVISIBLE
        request.tag = tag
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(this@CameraActivity).add(request)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PackageManager.PERMISSION_GRANTED in grantResults && requestCode == requestForSave) {
            saveSelectedImages()
        }
    }

    private fun saveSelectedImages() {
        var imagesSaved = 0
        cameras.indices.forEach {
            if (cameras[it] in selectedCameras) {
                val imageDrawable = listView.getViewByPosition(it).findViewById<ImageView>(R.id.source).drawable
                val title = listView.getViewByPosition(it).findViewById<TextView>(R.id.label)
                if (imageDrawable != null) {
                    if (saveImage((imageDrawable as BitmapDrawable).bitmap, title.text.toString())) {
                        imagesSaved++
                    }
                } else {
                    Log.w("CameraActivity", "$title is null")
                }
            }
        }

        Snackbar.make(
            listView,
            resources.getQuantityString(R.plurals.images_saved, imagesSaved, imagesSaved),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun saveImage(bitmapImage: Bitmap, fileName: String): Boolean {
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
                Log.e("StreetCams", e.message ?: e.stackTraceToString())
                return false
            }
        }

        // Now that we're finished, release the "pending" status, and allow other apps to play the audio track.
        imageDetails.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
        }
        resolver.update(songContentUri, imageDetails, null, null)
        return true
    }
}