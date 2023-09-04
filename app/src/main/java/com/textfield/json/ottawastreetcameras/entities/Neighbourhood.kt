package com.textfield.json.ottawastreetcameras.entities

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min


class Neighbourhood(values: JSONObject) : BilingualObject() {

    private var boundaries = ArrayList<List<LatLng>>()

    init {
        val properties = values.getJSONObject("properties")
        nameEn = properties.optString("Name") ?: ""
        nameFr = properties.optString("Name_FR") ?: nameEn
        id = properties.optInt("ONS_ID")

        try {
            val geometry = values.getJSONObject("geometry")
            var neighbourhoodZones = JSONArray()

            if (geometry.getString("type").equals("Polygon", true)) {
                neighbourhoodZones.put(geometry.getJSONArray("coordinates"))
            } else {
                neighbourhoodZones = geometry.getJSONArray("coordinates")
            }
            for (item in 0 until neighbourhoodZones.length()) {
                val neighbourhoodPoints = neighbourhoodZones.getJSONArray(item).getJSONArray(0)
                val list = (0 until neighbourhoodPoints.length()).map {
                    LatLng(
                        neighbourhoodPoints.getJSONArray(it).getDouble(1),
                        neighbourhoodPoints.getJSONArray(it).getDouble(0)
                    )
                }
                boundaries.add(list)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    //http://en.wikipedia.org/wiki/Point_in_polygon
    //https://stackoverflow.com/questions/26014312/identify-if-point-is-in-the-polygon
    fun containsCamera(camera: Camera): Boolean {
        var intersectCount = 0
        val cameraLocation = LatLng(camera.lat, camera.lon)

        for (points in boundaries) {
            for (j in 0 until points.size - 1) {
                if (onSegment(points[j], cameraLocation, points[j + 1])) {
                    return true
                }
                if (rayCastIntersect(cameraLocation, points[j], points[j + 1])) {
                    intersectCount++
                }
            }
        }
        // odd = inside, even = outside
        return (intersectCount % 2) == 1
    }

    private fun onSegment(a: LatLng, location: LatLng, b: LatLng): Boolean {
        // double division by 0 results in infinity
        val rise = b.latitude - a.latitude
        val run = b.longitude - a.longitude
        val slope = rise / run

        val rise2 = b.latitude - location.latitude
        val run2 = b.longitude - location.longitude
        val slope2 = rise2 / run2

        return location.longitude <= max(a.longitude, b.longitude) && location.longitude >= min(
            a.longitude,
            b.longitude
        ) && location.latitude <= max(a.latitude, b.latitude) && location.latitude >= min(
            a.latitude,
            b.latitude
        ) && slope2.absoluteValue == slope.absoluteValue
    }

    private fun rayCastIntersect(location: LatLng, a: LatLng, b: LatLng): Boolean {
        val aX = a.longitude
        val aY = a.latitude
        val bX = b.longitude
        val bY = b.latitude
        val locX = location.longitude
        val locY = location.latitude

        if ((aY > locY && bY > locY) || (aY < locY && bY < locY) || (aX < locX && bX < locX)) {
            // a and b can't both be above or below pt.y, and a or b must be east of pt.x
            return false
        }

        // vertical line
        if (aX == bX) {
            return aX >= locX
        }

        val rise = aY - bY
        val run = aX - bX
        val slope = rise / run

        val c = -slope * aX + aY // c = -mx + y
        val x = (locY - c) / slope

        return x >= locX
    }
}