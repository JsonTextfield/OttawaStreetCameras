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
    var marker: Marker? = null

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

    constructor(parcel: Parcel) {
        nameEn = parcel.readString()!!
        nameFr = parcel.readString()!!
        owner = parcel.readString()!!
        lat = parcel.readDouble()
        lng = parcel.readDouble()
        id = parcel.readInt()
        num = parcel.readInt()
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

    override fun toString(): String {
        return getSortableName()
    }

    companion object CREATOR : Parcelable.Creator<Camera> {
        override fun createFromParcel(`in`: Parcel): Camera {
            return Camera(`in`)
        }

        override fun newArray(size: Int): Array<Camera?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Camera) return false
        return (nameEn == other.nameEn)
                && (nameFr == other.nameFr)
                && (lat == other.lat)
                && (lng == other.lng)
                && (owner == other.owner)
                && (id == other.id)
                && (num == other.num)
    }

    override fun hashCode(): Int {
        var result = owner.hashCode()
        result = 31 * result + lat.hashCode()
        result = 31 * result + lng.hashCode()
        result = 31 * result + num
        result = 31 * result + isFavourite.hashCode()
        result = 31 * result + isVisible.hashCode()
        result = 31 * result + neighbourhood.hashCode()
        result = 31 * result + (marker?.hashCode() ?: 0)
        return result
    }
}
