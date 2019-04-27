package com.textfield.json.ottawastreetcameras

import android.widget.Filter
import com.textfield.json.ottawastreetcameras.entities.Camera

abstract class CameraFilter(private val allCameras: List<Camera>) : Filter() {

    abstract fun refresh(list: ArrayList<Camera>)

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        refresh(results.values as ArrayList<Camera>)
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {

        val filteredResults = when {
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

        val results = FilterResults()
        results.values = filteredResults
        return results
    }
}