package com.textfield.json.ottawastreetcameras.adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.textfield.json.ottawastreetcameras.NeighbourhoodFilter
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood

class NeighbourhoodAdapter(context: Context, list: AbstractList<Neighbourhood>): ArrayAdapter<Neighbourhood>(context, 0, list) {

    override fun getFilter(): Filter {
return object : NeighbourhoodFilter(){

}
    }
}
