package com.textfield.json.ottawastreetcameras.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.textfield.json.ottawastreetcameras.Camera
import com.textfield.json.ottawastreetcameras.R
import java.util.*

class MapsActivity : FragmentActivity(), OnMapReadyCallback, ActionMode.Callback {

    private val selectedCameras = ArrayList<Camera>()
    private val markers = ArrayList<Marker>()
    private val maxCameras = 4
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
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

        googleMap.setOnInfoWindowLongClickListener {marker ->

            actionMode = startActionMode(this@MapsActivity)
            selectedCameras.add(marker.tag as Camera)
            selectMarker(marker, true)
        }

        googleMap.setOnInfoWindowClickListener { marker ->
            val camera = marker.tag as Camera
            if (actionMode != null) {
                if (!selectedCameras.contains(camera) && selectedCameras.size < maxCameras) {
                    selectedCameras.add(marker.tag as Camera)
                    selectMarker(marker, true)
                } else {
                    selectedCameras.remove(marker.tag as Camera)
                    selectMarker(marker, false)

                    if (selectedCameras.isEmpty()) {
                        actionMode?.finish()
                    }
                }
            } else {
                selectedCameras.add(marker.tag as Camera)
                val intent = Intent(this@MapsActivity, CameraActivity::class.java)
                intent.putParcelableArrayListExtra("cameras", selectedCameras)
                startActivity(intent)
            }
        }
        val builder = LatLngBounds.Builder()

        //add a marker for every camera available
        for (camera in cameras) {
            val m = googleMap.addMarker(MarkerOptions().position(LatLng(camera.lat, camera.lng)).title(camera.getName()))
            m.tag = camera
            builder.include(m.position)
        }

        googleMap.setOnMapLoadedCallback {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50))
        }

    }

    fun back(v: View) {
        finish()
    }

    fun selectMarker(marker: Marker, boolean: Boolean) {
        if (boolean) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        } else {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker())
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        for (marker in markers) {
            if (selectedCameras.contains(marker.tag)) {
                selectMarker(marker, false)
            }
        }
        selectedCameras.clear()
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.open_cameras) {
            val intent = Intent(this@MapsActivity, CameraActivity::class.java)
            intent.putParcelableArrayListExtra("cameras", selectedCameras)
            startActivity(intent)
            return true
        }
        return false
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode!!.menuInflater.inflate(R.menu.contextual_menu, menu)
        return true
    }
}
