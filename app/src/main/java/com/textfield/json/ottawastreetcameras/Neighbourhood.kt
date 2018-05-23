package com.textfield.json.ottawastreetcameras

import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class Neighbourhood {
    private var name = ""
    private var nameFr = ""
    var id = 0
        private set

    var boundaries = ArrayList<LatLng>()

    constructor(vals: JSONObject) {
        try {
            val props = vals.getJSONObject("properties")
            name = props.getString("Name")
            nameFr = props.getString("Name_FR")
            id = props.getInt("ONS_ID")
            val neighbourhoodPoints = vals.getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0)
            (0 until neighbourhoodPoints.length()).map {
                boundaries.add(LatLng(neighbourhoodPoints.getJSONArray(it).getDouble(1),
                        neighbourhoodPoints.getJSONArray(it).getDouble(0)))
            }

        } catch (e: JSONException) {
            nameFr = ""
            name = nameFr
            id = 0
            boundaries = ArrayList<LatLng>()
        }

    }

    fun getName(): String {
        return if (Locale.getDefault().displayLanguage.contains("fr")) nameFr else name
    }

    override fun toString(): String {
        return getName()
    }
}