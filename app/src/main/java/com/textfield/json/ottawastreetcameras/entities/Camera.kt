package com.textfield.json.ottawastreetcameras.entities

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject
import kotlin.math.roundToInt

/**
 * Created by Jason on 25/04/2016.
 */
class Camera : Parcelable {
    var isFavourite = false
    var isVisible = true
    var isSelected = false
    var distance = -1

    var id = ""
        private set

    private var _name = BilingualObject()
    val name: String
        get() = _name.name
    val sortableName: String
        get() = _name.sortableName

    private var _neighbourhood = BilingualObject()
    val neighbourhood: String
        get() = _neighbourhood.name

    var lat = 0.0
        private set
    var lon = 0.0
        private set

    private var _url = ""
    val url: String
        get() = "$_url&timems=${System.currentTimeMillis()}"

    val preview: String
        get() = _url

    val distanceString: String
        get() {
            var distance = this.distance.toDouble()

            return if (distance < 0) {
                ""
            }
            else if (distance > 9000e3) {
                ">9000\nkm"
            }
            else if (distance >= 100e3) {
                "${(distance / 1000).roundToInt()}\nkm"
            }
            else if (distance >= 500) {
                distance = (distance / 100.0).roundToInt().toDouble() / 10
                "$distance\nkm"
            }
            else {
                "${distance.roundToInt()}\nm"
            }
        }

    constructor()

    constructor(parcel: Parcel) {
        id = parcel.readString() ?: ""
        _name = BilingualObject(en = parcel.readString() ?: "", fr = parcel.readString() ?: "")
        _neighbourhood = BilingualObject(en = parcel.readString() ?: "", fr = parcel.readString() ?: "")
        lat = parcel.readDouble()
        lon = parcel.readDouble()
        _url = parcel.readString() ?: ""
    }

    private constructor(
        id: String,
        nameEn: String,
        nameFr: String,
        neighbourhoodEn: String,
        neighbourhoodFr: String,
        lat: Double,
        lon: Double,
        url: String,
    ) {
        this.id = id
        _name = BilingualObject(en = nameEn, fr = nameFr)
        _neighbourhood = BilingualObject(en = neighbourhoodEn, fr = neighbourhoodFr)
        this.lat = lat
        this.lon = lon
        _url = url
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(id)
        parcel.writeString(_name.en)
        parcel.writeString(_name.fr)
        parcel.writeString(_neighbourhood.en)
        parcel.writeString(_neighbourhood.fr)
        parcel.writeDouble(lat)
        parcel.writeDouble(lon)
        parcel.writeString(_url)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Camera) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String = name

    companion object CREATOR : Parcelable.Creator<Camera> {
        override fun createFromParcel(`in`: Parcel): Camera {
            return Camera(`in`)
        }

        override fun newArray(size: Int): Array<Camera?> {
            return arrayOfNulls(size)
        }

        fun fromJson(jsonObject: JSONObject): Camera {
            return Camera(
                id = jsonObject.optString("id"),
                nameEn = jsonObject.optString("nameEn"),
                nameFr = jsonObject.optString("nameFr"),
                neighbourhoodEn = jsonObject.optString("neighbourhoodEn"),
                neighbourhoodFr = jsonObject.optString("neighbourhoodFr"),
                lat = jsonObject.getJSONObject("location").optDouble("lat"),
                lon = jsonObject.getJSONObject("location").optDouble("lon"),
                url = jsonObject.optString("url"),
            )
        }
    }
}
