package com.textfield.json.ottawastreetcameras.entities

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * Created by Jason on 25/04/2016.
 */
class Camera : BilingualObject, Parcelable {
    var owner = ""
        private set
    var lat = 0.0
        private set
    var lon = 0.0
        private set
    var num = 0
        private set

    var isFavourite = false
    var isVisible = true
    var isSelected = false
    var neighbourhood = ""
    var distance = -1

    val url: String
        get() = "https://traffic.ottawa.ca/beta/camera?id=$num&timems=${System.currentTimeMillis()}"

    constructor()

    constructor(jsonObject: JSONObject) {
        nameEn = jsonObject.optString("description") ?: ""
        nameFr = jsonObject.optString("descriptionFr") ?: nameEn
        owner = jsonObject.optString("type") ?: ""
        id = jsonObject.optInt("id")
        num = jsonObject.optInt("number")
        lat = jsonObject.optDouble("latitude")
        lon = jsonObject.optDouble("longitude")
    }

    constructor(parcel: Parcel) {
        nameEn = parcel.readString()!!
        nameFr = parcel.readString()!!
        owner = parcel.readString()!!
        lat = parcel.readDouble()
        lon = parcel.readDouble()
        id = parcel.readInt()
        num = parcel.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(nameEn)
        parcel.writeString(nameFr)
        parcel.writeString(owner)
        parcel.writeDouble(lat)
        parcel.writeDouble(lon)
        parcel.writeInt(id)
        parcel.writeInt(num)
    }

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Camera) return false
        return id == other.id && num == other.num
    }

    override fun hashCode(): Int {
        return 31 * id + num
    }

    companion object CREATOR : Parcelable.Creator<Camera> {
        override fun createFromParcel(`in`: Parcel): Camera {
            return Camera(`in`)
        }

        override fun newArray(size: Int): Array<Camera?> {
            return arrayOfNulls(size)
        }
    }
}
