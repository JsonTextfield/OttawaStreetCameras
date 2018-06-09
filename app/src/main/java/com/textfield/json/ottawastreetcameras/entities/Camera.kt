package com.textfield.json.ottawastreetcameras.entities

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by Jason on 25/04/2016.
 */
class Camera : Parcelable {
    private var name = ""
    private var nameFr = ""
    var owner = ""
        private set
    var lat = 0.0
        private set
    var lng = 0.0
        private set
    var num = 0
        private set
    var id = 0
        private set
    var isFavourite = false
    var isVisible = true

    var neighbourhood = ""

    constructor(vals: JSONObject) {
        try {
            name = vals.getString("description")
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
            owner = nameFr
            name = owner
            id = 0
            num = 0
            lng = 0.0
            lat = 0.0
        }

    }

    constructor(`in`: Parcel) {
        name = `in`.readString()
        nameFr = `in`.readString()
        owner = `in`.readString()
        lat = `in`.readDouble()
        lng = `in`.readDouble()
        id = `in`.readInt()
        num = `in`.readInt()
    }

    constructor(cursor: Cursor) {
        name = cursor.getString(cursor.getColumnIndex("name"))
        nameFr = cursor.getString(cursor.getColumnIndex("nameFr"))
        owner = cursor.getString(cursor.getColumnIndex("owner"))
        lat = cursor.getDouble(cursor.getColumnIndex("latitude"))
        lng = cursor.getDouble(cursor.getColumnIndex("longitude"))
        id = cursor.getInt(cursor.getColumnIndex("id"))
        num = cursor.getInt(cursor.getColumnIndex("num"))
        if (owner == "MTO") {
            num += 2000
        }
    }

    fun getName(): String {
        return if (Locale.getDefault().displayLanguage.contains("fr")) nameFr else name
    }

    fun getSortableName(): String {
        return getName().replace(Regex("\\W"), "").toUpperCase()
    }

    override fun toString(): String {
        return getName()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(name)
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
