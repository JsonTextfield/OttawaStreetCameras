package com.textfield.json.ottawastreetcameras

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        val cameras = intent.getParcelableArrayListExtra<Camera>("cameras")
        googleMap.setOnInfoWindowClickListener { marker ->
            val b = Bundle()
            val cams = ArrayList(Arrays.asList(marker.tag as Camera))
            b.putParcelableArrayList("cameras", cams)

            val intent = Intent(this@MapsActivity, CameraActivity::class.java)
            intent.putExtras(b)
            startActivity(intent)
        }
        val builder = LatLngBounds.Builder()
        val padding = 50 // offset from edges of the map in pixels

        // Add a marker in Sydney and move the camera
        for (camera in cameras) {
            val m = googleMap.addMarker(MarkerOptions().position(LatLng(camera.lat, camera.lng)).title(camera.name))
            m.tag = camera
            builder.include(m.position)

        }
        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        googleMap.animateCamera(cu)


    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onMenuItemSelected(featureId, item)
    }
}
