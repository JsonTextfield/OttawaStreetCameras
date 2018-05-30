package com.textfield.json.ottawastreetcameras

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class Neighbourhood {
    private var name = ""
    private var nameFr = ""
    var id = 0
        private set

    var boundaries = ArrayList<ArrayList<LatLng>>()
        private set

    constructor(vals: JSONObject) {
        try {
            val props = vals.getJSONObject("properties")
            name = props.getString("Name")
            nameFr = props.getString("Name_FR")
            id = props.getInt("ONS_ID")

            val geo = vals.getJSONObject("geometry")
            var neighbourhoodZones = JSONArray()

            if (geo.getString("type") == "Polygon") {
                neighbourhoodZones.put(geo.getJSONArray("coordinates").getJSONArray(0))
            } else {
                neighbourhoodZones = geo.getJSONArray("coordinates")
            }
            (0 until neighbourhoodZones.length()).forEach {
                val neighbourhoodPoints = neighbourhoodZones.getJSONArray(it).getJSONArray(0)
                val list = (0 until neighbourhoodPoints.length()).map {
                    LatLng(neighbourhoodPoints.getJSONArray(it).getDouble(1), neighbourhoodPoints.getJSONArray(it).getDouble(0))
                }
                boundaries.add(ArrayList<LatLng>(list))
            }

        } catch (e: JSONException) {
            nameFr = ""
            name = nameFr
            id = 0
            boundaries = ArrayList<ArrayList<LatLng>>()
        }
    }

    //http://en.wikipedia.org/wiki/Point_in_polygon
    //https://stackoverflow.com/questions/26014312/identify-if-point-is-in-the-polygon
    fun containsCamera(camera: Camera): Boolean {
        var intersectCount = 0
        val cameraLocation = LatLng(camera.lat, camera.lng)

        for (vertices in boundaries) {
            for (j in 0 until vertices.size - 1) {
                if (rayCastIntersect(cameraLocation, vertices[j], vertices[j + 1])) {
                    intersectCount++
                }
            }
        }
        return ((intersectCount % 2) == 1) // odd = inside, even = outside
    }

    private fun rayCastIntersect(location: LatLng, vertA: LatLng, vertB: LatLng): Boolean {

        val aY = vertA.latitude
        val bY = vertB.latitude
        val aX = vertA.longitude
        val bX = vertB.longitude
        val pY = location.latitude
        val pX = location.longitude

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY) || (aX < pX && bX < pX)) {
            return false // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        val m = (aY - bY) / (aX - bX) // Rise over run
        val bee = (-aX) * m + aY // y = mx + b
        val x = (pY - bee) / m // algebra is neat!

        return x > pX
    }

    fun getName(): String {
        return if (Locale.getDefault().displayLanguage.contains("fr")) nameFr else name
    }

    override fun toString(): String {
        return getName()
    }
}