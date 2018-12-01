package com.textfield.json.ottawastreetcameras.activities

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.adapters.ImageAdapter
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.*
import java.net.URL
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

class CameraActivity : GenericActivity() {
    private var socketFactory: SSLSocketFactory? = null
    private var cameras = ArrayList<Camera>()
    private var timers = ArrayList<CameraRunnable>()
    private val handler = Handler()
    private lateinit var queue: RequestQueue
    private var sessionId = ""
    private val tag = "camera"
    private lateinit var imageAdapter: ImageAdapter
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameras = intent.getParcelableArrayListExtra<Camera>("cameras")
        imageAdapter = ImageAdapter(this, cameras)
        image_listView.adapter = imageAdapter
        createSSL()
        getSessionId()
    }

    override fun onDestroy() {
        queue.cancelAll(tag)
        for (timer in timers) {
            handler.removeCallbacks(timer)
        }
        super.onDestroy()
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap, id: Int) {
        val mypath = File(ContextWrapper(applicationContext).getDir("images", Context.MODE_PRIVATE), "$id.jpg");

        var fos: FileOutputStream? = null;
        try {
            fos = FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (e: Exception) {
            e.printStackTrace();
        } finally {
            try {
                fos?.close();
            } catch (e: IOException) {
                e.printStackTrace();
            }
        }
    }

    private fun createSSL() {
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        val caInput: InputStream = BufferedInputStream(assets.open("certificate.cer"))
        val ca: X509Certificate = caInput.use {
            cf.generateCertificate(it) as X509Certificate
        }

        // Create a KeyStore containing our trusted CAs
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType).apply {
            load(null, null)
            setCertificateEntry("ca", ca)
        }

        // Create a TrustManager that trusts the CAs inputStream our KeyStore
        val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
        val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
            init(keyStore)
        }

        // Create an SSLContext that uses our TrustManager
        val context: SSLContext = SSLContext.getInstance("TLS").apply {
            init(null, tmf.trustManagers, null)
        }
        socketFactory = context.socketFactory

        queue = Volley.newRequestQueue(this, object : HurlStack() {
            override fun createConnection(url: URL): HttpsURLConnection {
                val connection = url.openConnection() as HttpsURLConnection
                connection.sslSocketFactory = context.socketFactory
                connection.setRequestProperty("Cookie", sessionId)
                return connection
            }
        })
    }

    private inner class CameraRunnable(index: Int) : Runnable {
        val i = index
        override fun run() {
            download(i)
            handler.postDelayed(this, 500L)
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
        val url = "https://traffic.ottawa.ca/map/camera?id=" + imageAdapter.getItem(index).num

        val request = ImageRequest(url, Response.Listener<Bitmap> { response ->
            camera_progress_bar.visibility = View.INVISIBLE
            try {
                bmImage.setImageResource(android.R.color.transparent)
                bmImage.setImageBitmap(response)
            } catch (e: NullPointerException) {
            }
        }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, Response.ErrorListener {
            println(it)
        })

        request.tag = tag
        queue.add(request)
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
            print(it)
            showErrorDialogue(this)
        }) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                sessionId = response.headers["Set-Cookie"]!!
                return Response.success(sessionId, HttpHeaderParser.parseCacheHeaders(response))
            }
        }
        sessionRequest.tag = tag
        queue.add(sessionRequest)
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


