package com.textfield.json.ottawastreetcameras.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ImageView
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.textfield.json.ottawastreetcameras.Camera
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.adapters.ImageAdapter
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.view.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class CameraActivity : AppCompatActivity() {

    private var cameras = ArrayList<Camera>()
    private var timers = ArrayList<CameraRunnable>()
    private var handler = Handler()
    private var queue: RequestQueue? = null
    private var sessionId = ""
    private val tag = "camera"

    fun download(index: Int) {
        val url = "http://traffic.ottawa.ca/map/camera?id=" + cameras[index].num

        val request = ImageRequest(url, Response.Listener<Bitmap> { response ->
            try {
                val bmImage = image_listView.getChildAt(index).findViewById(R.id.source) as ImageView
                bmImage.setImageBitmap(response)
            } catch (e: NullPointerException) {
            }
        }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, Response.ErrorListener { })
        request.tag = tag
        queue!!.add(request)
    }

    fun getSessionId() {
        val url = "http://traffic.ottawa.ca/map"
        val sessionRequest = object : StringRequest(url, Response.Listener { response ->
            for (i in 0 until cameras.size) {
                val cameraRunnable = CameraRunnable(i)
                cameraRunnable.run()
                timers.add(cameraRunnable)
            }
        }, Response.ErrorListener {
            val builder = AlertDialog.Builder(this@CameraActivity)

            builder.setTitle(resources.getString(R.string.no_network_title)).setMessage(resources.getString(R.string.no_network_content))
                    .setPositiveButton("OK") { _, _ -> finish() }
            val dialog = builder.create()
            dialog.show()
        }) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                sessionId = response.headers.get("Set-Cookie")!!
                return Response.success(sessionId, HttpHeaderParser.parseCacheHeaders(response))

            }
        }
        sessionRequest.tag = tag
        queue!!.add(sessionRequest)
    }


    private inner class CameraRunnable(index: Int) : Runnable {
        val i = index
        override fun run() {
            download(i)
            handler.postDelayed(this, 500L)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        queue = Volley.newRequestQueue(this, object : HurlStack() {
            @Throws(IOException::class)
            override fun createConnection(url: URL): HttpURLConnection {

                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("Cookie", sessionId)

                return connection
            }
        })

        camera_toolbar.title = ""
        val title = camera_toolbar.textView

        setSupportActionBar(camera_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val bundle = intent.extras
        cameras = bundle!!.getParcelableArrayList<Camera>("cameras")

        val allTitles = StringBuilder()
        for (camera in cameras) {
            allTitles.append(camera.getName())
            allTitles.append(", ")
        }
        var s = ""
        if (allTitles.length > 2) {
            s = allTitles.substring(0, allTitles.length - 2)
        }
        title.text = s
        image_listView.adapter = ImageAdapter(this, cameras)

        getSessionId()
        //GetSessionIdTask().execute()
    }

    override fun onDestroy() {
        queue!!.cancelAll(tag)
        for (timer in timers) {
            handler.removeCallbacks(timer)
        }
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    /*    private inner class GetSessionIdTask : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg voids: Void): Boolean? {
        try {
            val oracle = URL("http://traffic.ottawa.ca/map")
            val urlConnection = oracle.openConnection() as HttpURLConnection
            sessionId = urlConnection.headerFields["Set-Cookie"]!![0]

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            return false
        }

        return true
    }

    override fun onPostExecute(b: Boolean?) {
        if (b!!) {

            for (i in 0 until cameras.size) {
                val cameraRunnable = CameraRunnable(i)
                cameraRunnable.run()
                timers.add(cameraRunnable)
            }
        }
    }
}*/
    /*private inner class DownloadImageTask(internal var bmImage: ImageView, internal var camera: Camera) : AsyncTask<Void, Void, Bitmap>() {

        override fun doInBackground(vararg v: Void): Bitmap? {

            val url = "http://traffic.ottawa.ca/map/camera?id=" + camera.num
            var mIcon11: Bitmap? = null
            try {
                val myUrl = URL(url)
                val urlConnection = myUrl.openConnection()
                urlConnection.setRequestProperty("Cookie", sessionId)

                val `in` = urlConnection.getInputStream()

                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
            }
            return mIcon11

        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                bmImage.setImageBitmap(result)
            }
        }

    }*/

}
