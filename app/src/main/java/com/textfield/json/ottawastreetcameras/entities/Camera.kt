package com.textfield.json.ottawastreetcameras.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
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
        private set
    var isVisible = true
        private set

    var neighbourhood = ""
    var marker: Marker? = null

    constructor(jsonObject: JSONObject) {
        nameEn = if (jsonObject.isNull("description")) "" else jsonObject.getString("description")
        nameFr = if (jsonObject.isNull("descriptionFr")) nameEn else jsonObject.getString("descriptionFr")
        owner = if (jsonObject.isNull("type")) "" else jsonObject.getString("type")
        id = if (jsonObject.isNull("id")) 0 else jsonObject.getInt("id")
        num = if (jsonObject.isNull("number")) 0 else jsonObject.getInt("number")
        lat = if (jsonObject.isNull("latitude")) 0.0 else jsonObject.getDouble("latitude")
        lon = if (jsonObject.isNull("longitude")) 0.0 else jsonObject.getDouble("longitude")
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

    fun setVisible(b: Boolean) {
        marker?.isVisible = b
        isVisible = b
    }

    fun setFavourite(b: Boolean) {
        isFavourite = b
        if (b)
            marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        else
            marker?.setIcon(BitmapDescriptorFactory.defaultMarker())
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
        return  (id == other.id) && (num == other.num)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + num
        return result
    }
}
