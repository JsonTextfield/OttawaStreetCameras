package com.textfield.json.ottawastreetcameras.adapters.filters

import android.widget.Filter
import com.textfield.json.ottawastreetcameras.entities.Camera

abstract class CameraFilter(private val allCameras: List<Camera>) : Filter() {
    private val favouritePrefix = "f:"
    private val hiddenPrefix = "h:"
    private val neighbourhoodPrefix = "n:"

    abstract fun onPublishResults(list: ArrayList<Camera>)

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        onPublishResults(results.values as ArrayList<Camera>)
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        val results = FilterResults()
        results.values = when {
            constraint.startsWith(favouritePrefix, true) -> allCameras.filter {
                it.name.contains(constraint.removePrefix(favouritePrefix).trim(), true) && it.isFavourite
            }
            constraint.startsWith(hiddenPrefix, true) -> allCameras.filter {
                it.name.contains(constraint.removePrefix(hiddenPrefix).trim(), true) && !it.isVisible
            }
            constraint.startsWith(neighbourhoodPrefix, true) -> allCameras.filter {
                it.neighbourhood.contains(constraint.removePrefix(neighbourhoodPrefix).trim(), true)
            }
            else -> allCameras.filter {
                it.name.contains(constraint, true) && it.isVisible
            }
        }
        return results
    }
}