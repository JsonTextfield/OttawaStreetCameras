package com.textfield.json.ottawastreetcameras

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.textfield.json.ottawastreetcameras.R.id.camera_toolbar
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.view.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RUNNING = true
        setContentView(R.layout.activity_camera)

        camera_toolbar.title = ""
        val title = camera_toolbar.textView

        setSupportActionBar(camera_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val bundle = intent.extras
        val cameras = bundle!!.getParcelableArrayList<Camera>("cameras")

        val allTitles = StringBuilder()
        for (camera in cameras!!) {
            if (Locale.getDefault().displayLanguage.contains("fr"))
                allTitles.append(camera.nameFr)
            else
                allTitles.append(camera.name)
            allTitles.append(", ")
        }
        var s = ""
        if (allTitles.length > 2) {
            s = allTitles.substring(0, allTitles.length - 2)
        }
        title.text = s
        GetSessionIdTask(ArrayList(cameras)).execute()
    }

    private inner class GetSessionIdTask(internal var cameras: ArrayList<Camera>) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg voids: Void): Boolean? {
            try {
                val oracle = URL("http://traffic.ottawa.ca/map")
                val urlConnection = oracle.openConnection() as HttpURLConnection
                CameraActivity.SESSION_ID = urlConnection.headerFields["Set-Cookie"]!![0]

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                RUNNING = false
                return RUNNING
            }

            return true
        }

        override fun onPostExecute(b: Boolean?) {
            if (RUNNING) {
                for (camera in cameras) {
                    val imageView = ImageView(this@CameraActivity)
                    imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                    imageView.adjustViewBounds = true
                    imageView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                    layout.addView(imageView)
                    DownloadImageTask(imageView, camera).execute()
                }
            } else {
                errortext.visibility = View.VISIBLE
            }
        }
    }


    private inner class DownloadImageTask(internal var bmImage: ImageView, internal var camera: Camera) : AsyncTask<Void, Void, Bitmap>() {

        override fun doInBackground(vararg v: Void): Bitmap? {

            val url = "http://traffic.ottawa.ca/map/camera?id=" + camera.num
            var mIcon11: Bitmap? = null
            try {
                val myUrl = URL(url)
                val urlConnection = myUrl.openConnection()
                urlConnection.setRequestProperty("Cookie", CameraActivity.SESSION_ID)

                val `in` = urlConnection.getInputStream()

                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                //Log.e("Error", e.getMessage());
                //e.printStackTrace();
            }

            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                //e.printStackTrace();
            }

            return mIcon11

        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                bmImage.setImageBitmap(result)
            } else {
                errortext.visibility = View.VISIBLE
            }
            //Log.w("STREETCAM", "updated");
            if (RUNNING) {
                DownloadImageTask(bmImage, camera).execute()
            }
        }

    }

    override fun onBackPressed() {
        RUNNING = false
        super.onBackPressed()
    }

    override fun onDestroy() {
        RUNNING = false
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private var RUNNING: Boolean = false
        private var SESSION_ID: String? = null
    }
}
