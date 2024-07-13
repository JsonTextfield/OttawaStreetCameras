package com.textfield.json.ottawastreetcameras.entities

import android.os.Parcel
import android.os.Parcelable
import kotlin.math.roundToInt

/**
 * Created by Jason on 25/04/2016.
 */
data class Camera(
    val id: String = "",
    private val _name: BilingualObject = BilingualObject(),
    private val _neighbourhood: BilingualObject = BilingualObject(),
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    private val _url: String = "",
) : Parcelable {
    var isFavourite = false
    var isVisible = true
    var isSelected = false
    var distance = -1

    val name: String get() = _name.name
    val sortableName: String get() = _name.sortableName
    val neighbourhood: String get() = _neighbourhood.name
    val url: String get() = "$_url&timems=${System.currentTimeMillis()}"
    val preview: String get() = _url

    val distanceString: String
        get() {
            val distance = this.distance.toDouble()
            return when {
                distance < 0 -> ""
                distance > 9_000_000 -> ">9000\nkm"
                distance >= 100_000 -> "${(distance / 1000).roundToInt()}\nkm"
                distance >= 500 -> "${(distance / 100).roundToInt() / 10.0}\nkm"
                else -> "${distance.roundToInt()}\nm"
            }
        }

    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        _name = BilingualObject(en = parcel.readString() ?: "", fr = parcel.readString() ?: ""),
        _neighbourhood = BilingualObject(en = parcel.readString() ?: "", fr = parcel.readString() ?: ""),
        lat = parcel.readDouble(),
        lon = parcel.readDouble(),
        _url = parcel.readString() ?: "",
    )

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

    override fun toString(): String = name

    companion object CREATOR : Parcelable.Creator<Camera> {
        override fun createFromParcel(`in`: Parcel): Camera {
            return Camera(`in`)
        }

        override fun newArray(size: Int): Array<Camera?> {
            return arrayOfNulls(size)
        }
    }
}
