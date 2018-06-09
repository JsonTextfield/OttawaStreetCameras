package com.textfield.json.ottawastreetcameras

import android.widget.Filter
import com.textfield.json.ottawastreetcameras.entities.Camera

abstract class CameraFilter(private val allCameras: List<Camera>) : Filter() {

    abstract fun refresh(list: ArrayList<Camera>)

    override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
        refresh(results.values as ArrayList<Camera>)
    }

    override fun performFiltering(constraint: CharSequence): Filter.FilterResults {

        val filteredResults = when {
            constraint.startsWith("f: ") -> allCameras.filter {
                it.getName().contains(constraint.removePrefix("f: "), true) && it.isFavourite
            }
            constraint.startsWith("h: ") -> allCameras.filter {
                it.getName().contains(constraint.removePrefix("h: "), true) && !it.isVisible
            }
            constraint.startsWith("n: ") -> allCameras.filter {
                it.neighbourhood.contains(constraint.removePrefix("n: "), true)
            }
            constraint . startsWith ("!f: ") -> allCameras.filter {
                !it.getName().contains(constraint.removePrefix("!f: "), true) && it.isFavourite
            }
            constraint.startsWith("!h: ") -> allCameras.filter {
                !it.getName().contains(constraint.removePrefix("!h: "), true) && !it.isVisible
            }
            constraint.startsWith("!n: ") -> allCameras.filter {
                !it.neighbourhood.contains(constraint.removePrefix("!n: "), true)
            }
            constraint.startsWith("! ") -> allCameras.filter {
                !it.getName().contains(constraint.removePrefix("! "), true) && it.isVisible
            }
            else -> allCameras.filter {
                it.getName().contains(constraint, true) && it.isVisible
            }
        }

        val results = Filter.FilterResults()
        results.values = filteredResults

        return results
    }
}