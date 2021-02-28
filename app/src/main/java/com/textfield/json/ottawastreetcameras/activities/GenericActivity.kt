package com.textfield.json.ottawastreetcameras.activities

import android.content.Context
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SectionIndexListView
import com.textfield.json.ottawastreetcameras.adapters.CameraAdapter
import com.textfield.json.ottawastreetcameras.entities.Camera

abstract class GenericActivity : AppCompatActivity(), AbsListView.MultiChoiceModeListener {
    companion object {
        const val prefNameHidden = "hidden"
        const val prefNameFavourites = "favourites"
    }

    protected var actionMode: ActionMode? = null
    lateinit var listView: ListView
    var cameras = ArrayList<Camera>()
    val selectedCameras = ArrayList<Camera>()

    private lateinit var addFav: MenuItem
    private lateinit var removeFav: MenuItem
    private lateinit var hide: MenuItem
    private lateinit var unhide: MenuItem
    private lateinit var selectAll: MenuItem
    protected lateinit var showCameras: MenuItem
    protected lateinit var saveImage: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if (isNightModeOn()) R.style.AppTheme else R.style.AppTheme_Light)
        super.onCreate(savedInstanceState)
    }

    fun modifyPrefs(pref: String, selectedCameras: Collection<Camera>, willAdd: Boolean) {
        val sharedPrefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        val prefList = HashSet<String>(sharedPrefs.getStringSet(pref, HashSet<String>()))

        if (willAdd) {
            prefList.addAll(selectedCameras.map { it.num.toString() })
        } else {
            prefList.removeAll(selectedCameras.map { it.num.toString() })
        }
        sharedPrefs.edit().putStringSet(pref, prefList).apply()
    }

    fun isNightModeOn(): Boolean {
        val sharedPrefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("isNightModeOn", true)
    }

    fun setNightModeOn(value: Boolean) {
        val sharedPrefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("isNightModeOn", value).apply()
    }

    fun showErrorDialogue(context: Context) {
        val dialog = AlertDialog.Builder(context)
                .setTitle(resources.getString(R.string.no_network_title))
                .setMessage(resources.getString(R.string.no_network_content))
                .setPositiveButton("OK") { _, _ -> finish() }
                .setOnDismissListener { finish() }
                .create()
        dialog.show()
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        actionMode = mode
        actionMode?.menuInflater?.inflate(R.menu.contextual_menu, menu)
        showCameras = menu.findItem(R.id.open_cameras)
        selectAll = menu.findItem(R.id.select_all)
        removeFav = menu.findItem(R.id.remove_favourite)
        addFav = menu.findItem(R.id.add_favourites)
        unhide = menu.findItem(R.id.unhide)
        hide = menu.findItem(R.id.hide)
        saveImage = menu.findItem(R.id.save)
        for (i in 0 until menu.size()) {
            menu.getItem(i)?.icon?.setColorFilter(ContextCompat.getColor(this,
                    if (isNightModeOn()) android.R.color.white else android.R.color.black),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        cameras.filter { it in selectedCameras }.forEach { selectCamera(it) }
        actionMode = null
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_favourites -> {
                addRemoveFavs(true)
                return true
            }
            R.id.remove_favourite -> {
                addRemoveFavs(false)
                return true
            }
            R.id.hide -> {
                showOrHide(true)
                return true
            }
            R.id.unhide -> {
                showOrHide(false)
                return true
            }
            R.id.select_all -> {
                selectedCameras.clear()
                for (i in 0 until listView.adapter.count) {
                    listView.setItemChecked(i, true)
                }
                return true
            }
            else -> return false
        }
    }

    private fun addRemoveFavs(willAdd: Boolean) {
        modifyPrefs(prefNameFavourites, selectedCameras, willAdd)
        cameras.filter { it in selectedCameras }.forEach { it.setFavourite(willAdd) }

        if (listView.adapter is CameraAdapter) {
            for (i in 0 until listView.adapter.count) {
                if (listView.checkedItemPositions[i]) {
                    val view = listView.getViewByPosition(i)
                    val starImageView = view.findViewById<ImageView>(R.id.star)
                    starImageView.setImageDrawable(if (willAdd) {
                        ContextCompat.getDrawable(this, R.drawable.outline_star_white_18)
                    } else {
                        ContextCompat.getDrawable(this, R.drawable.outline_star_border_white_18)
                    })
                }
            }
        }
        addFav.isVisible = !willAdd
        removeFav.isVisible = willAdd
    }

    private fun showOrHide(willHide: Boolean) {
        modifyPrefs(prefNameHidden, selectedCameras, willHide)
        cameras.filter { it in selectedCameras }.forEach { it.setVisible(!willHide) }

        hide.isVisible = !willHide
        unhide.isVisible = willHide

        findViewById<SectionIndexListView>(R.id.section_index_listview)?.updateIndex()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (actionMode != null) {
            outState.putParcelableArrayList("selectedCameras", selectedCameras)
        }
        outState.putInt("firstVisibleListItem", listView.firstVisiblePosition)
        outState.putParcelableArrayList("cameras", cameras)
        super.onSaveInstanceState(outState)
    }

    protected open fun selectCamera(camera: Camera): Boolean {
        if (camera in selectedCameras) {
            selectedCameras.remove(camera)
            camera.marker?.setIcon(BitmapDescriptorFactory.defaultMarker())
            if (camera.isFavourite) {
                camera.marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            }
            if (selectedCameras.isEmpty()) {
                actionMode?.finish()
                return false
            }
        } else {
            selectedCameras.add(camera)
            if (actionMode != null) {
                camera.marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            }
        }
        actionMode?.let { actionMode ->
            actionMode.title = resources.getQuantityString(R.plurals.selectedCameras, selectedCameras.size, selectedCameras.size)

            val allFav = selectedCameras.map { it.isFavourite }.reduce { acc, b -> acc && b }
            addFav.isVisible = !allFav
            removeFav.isVisible = allFav

            val allInvis = selectedCameras.map { !it.isVisible }.reduce { acc, b -> acc && b }
            hide.isVisible = !allInvis
            unhide.isVisible = allInvis

            selectAll.isVisible = selectedCameras.size < listView.adapter.count
        }
        return camera in selectedCameras
    }

    override fun onItemCheckedStateChanged(mode: ActionMode?, position: Int, id: Long, checked: Boolean) {
        val adapter = listView.adapter as ArrayAdapter<Camera>
        if (!selectCamera(adapter.getItem(position)!!) && checked) {
            listView.setItemChecked(position, false)
        }
    }
}

//https://stackoverflow.com/questions/24811536/android-listview-get-item-view-by-position/24864536
fun ListView.getViewByPosition(pos: Int): View {
    val lastListItemPosition = firstVisiblePosition + childCount - 1
    return if (pos < firstVisiblePosition || pos > lastListItemPosition) {
        adapter.getView(pos, null, this)
    } else {
        getChildAt(pos - firstVisiblePosition)
    }
}
