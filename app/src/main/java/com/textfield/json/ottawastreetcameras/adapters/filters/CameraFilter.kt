package com.textfield.json.ottawastreetcameras.adapters.filters

import android.widget.Filter
import com.textfield.json.ottawastreetcameras.entities.Camera

abstract class CameraFilter(private val allCameras: List<Camera>) : Filter() {

    abstract fun onPublishResults(list: ArrayList<Camera>)

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        onPublishResults(results.values as ArrayList<Camera>)
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        val results = FilterResults()
        results.values = when {
            constraint.startsWith("f: ", true) -> allCameras.filter {
                it.getName().contains(constraint.removePrefix("f: "), true) && it.isFavourite
            }
            constraint.startsWith("h: ", true) -> allCameras.filter {
                it.getName().contains(constraint.removePrefix("h: "), true) && !it.isVisible
            }
            constraint.startsWith("n: ", true) -> allCameras.filter {
                it.neighbourhood.contains(constraint.removePrefix("n: "), true)
            }
            else -> allCameras.filter {
                it.getName().contains(constraint, true) && it.isVisible
            }
        }
        return results
    }
}