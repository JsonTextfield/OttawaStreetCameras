package com.textfield.json.ottawastreetcameras

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.Volley
import java.net.URL
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

class StreetCamsRequestQueue constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: StreetCamsRequestQueue? = null
        @Volatile
        var sessionId = ""
        @Volatile
        var cert : X509Certificate? = null

        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: StreetCamsRequestQueue(context).also {
                        INSTANCE = it
                    }
                }
    }

    private val httpRequestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }
    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        createSSL(context.applicationContext)
    }


    private fun createSSL(context: Context): RequestQueue {
        /*val destinationURL = URL("https://traffic.ottawa.ca/map/")
        val conn = destinationURL.openConnection() as HttpsURLConnection
        conn.connect()
        val certs = conn.serverCertificates
        val cert = certs.first { it is javax.security.cert.X509Certificate }*/

        // Create a KeyStore containing our trusted CAs
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType).apply {
            load(null, null)
            setCertificateEntry("ca", cert)
        }

        // Create a TrustManager that trusts the CAs inputStream our KeyStore
        val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
        val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
            init(keyStore)
        }

        // Create an SSLContext that uses our TrustManager
        val sslContext: SSLContext = SSLContext.getInstance("TLS").apply {
            init(null, tmf.trustManagers, null)
        }

        return Volley.newRequestQueue(context, object : HurlStack() {
            override fun createConnection(url: URL): HttpsURLConnection {
                val connection = url.openConnection() as HttpsURLConnection
                connection.sslSocketFactory = sslContext.socketFactory
                connection.setRequestProperty("Cookie", sessionId)
                return connection
            }
        })
    }

    fun <T> add(req: Request<T>) {
        requestQueue.add(req)
    }

    fun cancelAll(tag: String) {
        requestQueue.cancelAll(tag)
    }

    fun <T> addHttp(req: Request<T>) {
        httpRequestQueue.add(req)
    }
}