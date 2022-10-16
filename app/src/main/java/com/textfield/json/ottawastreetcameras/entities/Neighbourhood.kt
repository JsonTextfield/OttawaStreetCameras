package com.textfield.json.ottawastreetcameras.entities

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.max
import kotlin.math.min


class Neighbourhood(values: JSONObject) : BilingualObject() {

    private var boundaries = ArrayList<List<LatLng>>()

    init {
        try {
            val props = values.getJSONObject("properties")
            nameEn = props.getString("Name")
            nameFr = props.getString("Name_FR")
            if (nameFr.isBlank()) {
                nameFr = nameEn
            }
            id = props.getInt("ONS_ID")

            val geo = values.getJSONObject("geometry")
            var neighbourhoodZones = JSONArray()

            if (geo.getString("type") == "Polygon") {
                neighbourhoodZones.put(geo.getJSONArray("coordinates"))
            } else {
                neighbourhoodZones = geo.getJSONArray("coordinates")
            }
            (0 until neighbourhoodZones.length()).forEach { item ->
                val neighbourhoodPoints = neighbourhoodZones.getJSONArray(item).getJSONArray(0)
                val list = (0 until neighbourhoodPoints.length()).map {
                    LatLng(neighbourhoodPoints.getJSONArray(it).getDouble(1), neighbourhoodPoints.getJSONArray(it).getDouble(0))
                }
                boundaries.add(list)
            }

        } catch (e: JSONException) {
            nameFr = nameEn
        }
    }

    //http://en.wikipedia.org/wiki/Point_in_polygon
    //https://stackoverflow.com/questions/26014312/identify-if-point-is-in-the-polygon
    fun containsCamera(camera: Camera): Boolean {
        var intersectCount = 0
        val cameraLocation = LatLng(camera.lat, camera.lng)

        for (vertices in boundaries) {
            for (j in 0 until vertices.size - 1) {
                if (onSegment(vertices[j], cameraLocation, vertices[j + 1])) {
                    return true
                }
                if (rayCastIntersect(cameraLocation, vertices[j], vertices[j + 1])) {
                    intersectCount++
                }
            }
        }
        return ((intersectCount % 2) == 1) // odd = inside, even = outside
    }

    private fun onSegment(a: LatLng, location: LatLng, b: LatLng): Boolean {
        return location.longitude <= max(a.longitude, b.longitude)
                && location.longitude >= min(a.longitude, b.longitude)
                && location.latitude <= max(a.latitude, b.latitude)
                && location.latitude >= min(a.latitude, b.latitude)
    }

    private fun rayCastIntersect(location: LatLng, vertA: LatLng, vertB: LatLng): Boolean {

        val aY = vertA.latitude
        val bY = vertB.latitude
        val aX = vertA.longitude
        val bX = vertB.longitude
        val pY = location.latitude
        val pX = location.longitude

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY) || (aX < pX && bX < pX)) {
            return false
            // a and b can't both be above or below pt.y, and a or b must be east of pt.x
        }

        val m = (aY - bY) / (aX - bX) // Rise over run
        val bee = (-aX) * m + aY // y = mx + b
        val x = (pY - bee) / m // algebra is neat!

        return x > pX
    }
}