package com.textfield.json.ottawastreetcameras.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.Marker
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Jason on 25/04/2016.
 */
class Camera : BilingualObject, Parcelable {
    var owner = ""
        private set
    var lat = 0.0
        private set
    var lng = 0.0
        private set
    var num = 0
        private set
    var isFavourite = false
    var isVisible = true
        private set

    var neighbourhood = ""
    var marker : Marker? = null

    constructor(vals: JSONObject) {
        try {
            nameEn = vals.getString("description")
            nameFr = vals.getString("descriptionFr")
            owner = vals.getString("type")
            id = vals.getInt("id")
            num = vals.getInt("number")
            if (owner == "MTO") {
                num += 2000
            }
            lat = vals.getDouble("latitude")
            lng = vals.getDouble("longitude")
        } catch (e: JSONException) {
            nameFr = ""
            owner = ""
            nameEn = ""
            id = 0
            num = 0
            lng = 0.0
            lat = 0.0
        }
    }

    constructor(`in`: Parcel) {
        nameEn = `in`.readString()
        nameFr = `in`.readString()
        owner = `in`.readString()
        lat = `in`.readDouble()
        lng = `in`.readDouble()
        id = `in`.readInt()
        num = `in`.readInt()
    }

    fun setVisibility(b: Boolean) {
        marker?.isVisible = b
        isVisible = b
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(nameEn)
        parcel.writeString(nameFr)
        parcel.writeString(owner)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeInt(id)
        parcel.writeInt(num)
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
