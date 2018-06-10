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
            constraint.startsWith("f: ", true) -> allCameras.filter {
                it.getName().contains(constraint.removePrefix("f: ", true), true) && it.isFavourite
            }
            constraint.startsWith("h: ", true) -> allCameras.filter {
                it.getName().contains(constraint.removePrefix("h: ", true), true) && !it.isVisible
            }
            constraint.startsWith("n: ", true) -> allCameras.filter {
                it.neighbourhood.contains(constraint.removePrefix("n: ", true), true)
            }
            constraint . startsWith ("!f: ", true) -> allCameras.filter {
                !it.getName().contains(constraint.removePrefix("!f: ", true), true) && it.isFavourite
            }
            constraint.startsWith("!h: ", true) -> allCameras.filter {
                !it.getName().contains(constraint.removePrefix("!h: ", true), true) && !it.isVisible
            }
            constraint.startsWith("!n: ", true) -> allCameras.filter {
                !it.neighbourhood.contains(constraint.removePrefix("!n: ", true), true)
            }
            constraint.startsWith("! ", true) -> allCameras.filter {
                !it.getName().contains(constraint.removePrefix("! ", true), true) && it.isVisible
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
fun CharSequence.removePrefix(prefix: CharSequence, ignoreCase: Boolean): CharSequence{
    return toString().toLowerCase().removePrefix(prefix)
}