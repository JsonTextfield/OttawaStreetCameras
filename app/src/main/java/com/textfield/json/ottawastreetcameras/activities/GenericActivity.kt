package com.textfield.json.ottawastreetcameras.activities

import android.content.Context
import android.graphics.Color
import android.os.Build
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
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SectionIndexListView
import com.textfield.json.ottawastreetcameras.adapters.CameraAdapter
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood


abstract class GenericActivity : AppCompatActivity(), AbsListView.MultiChoiceModeListener {
    companion object {
        const val prefNameHidden = "hidden"
        const val prefNameFavourites = "favourites"
    }

    protected var actionMode: ActionMode? = null
    protected lateinit var listView: ListView
    protected lateinit var adapter: ArrayAdapter<Camera>
    protected var cameras = ArrayList<Camera>()
    protected val selectedCameras = ArrayList<Camera>()
    protected var previouslySelectedCameras = ArrayList<Camera>()
    protected var neighbourhoods = ArrayList<Neighbourhood>()

    protected lateinit var addFav: MenuItem
    protected lateinit var removeFav: MenuItem
    protected lateinit var hide: MenuItem
    protected lateinit var unhide: MenuItem
    protected lateinit var selectAll: MenuItem
    protected lateinit var showCameras: MenuItem
    protected lateinit var saveImage: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if (isNightModeOn()) R.style.AppTheme else R.style.AppTheme_Light)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (0 until adapter.count).forEach {
            if (adapter.getItem(it) in previouslySelectedCameras) {
                listView.setItemChecked(it, true)
            }
        }
        previouslySelectedCameras.clear()
    }

    protected fun loadPreviouslySelectedCameras(savedInstanceState: Bundle?) {
        val selectedCamerasIsNull = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState?.getParcelableArrayList("selectedCameras", Camera::class.java) != null
        } else {
            savedInstanceState?.getParcelableArrayList<Camera>("selectedCameras") != null
        }
        if (selectedCamerasIsNull) {
            previouslySelectedCameras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState?.getParcelableArrayList("selectedCameras", Camera::class.java)!!
            } else {
                savedInstanceState?.getParcelableArrayList("selectedCameras")!!
            }
            startActionMode(this)
        }
    }

    fun modifyPrefs(pref: String, selectedCameras: Collection<Camera>, willAdd: Boolean) {
        val sharedPrefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        val prefList = sharedPrefs.getStringSet(pref, HashSet<String>())?.toHashSet()

        if (willAdd) {
            prefList?.addAll(selectedCameras.map { it.num.toString() })
        } else {
            prefList?.removeAll(selectedCameras.map { it.num.toString() }.toSet())
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
        AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.no_network_title))
            .setMessage(resources.getString(R.string.no_network_content))
            .setPositiveButton("OK") { _, _ -> finish() }
            .setOnDismissListener { finish() }
            .create()
            .show()
    }

    protected fun tintMenuItemIcon(item: MenuItem) {
        item.icon?.let { drawable ->
            val wrapped = DrawableCompat.wrap(drawable)
            drawable.mutate()
            DrawableCompat.setTint(wrapped, if (isNightModeOn()) Color.WHITE else Color.BLACK)
            item.icon = drawable
        }
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
        (0 until menu.size()).forEach {
            tintMenuItemIcon(menu.getItem(it))
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        cameras.filter { it in selectedCameras }.forEach(::selectCamera)
        selectedCameras.clear()
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
                (0 until listView.adapter.count).forEach {
                    listView.setItemChecked(it, true)
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
            (0 until listView.adapter.count).forEach {
                if (listView.checkedItemPositions[it]) {
                    val view = listView.getViewByPosition(it)
                    val starImageView = view.findViewById<ImageView>(R.id.star)
                    val icon = if (willAdd) R.drawable.ic_baseline_star_18 else R.drawable.ic_baseline_star_border_18
                    starImageView.setImageDrawable(ContextCompat.getDrawable(this, icon))
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
            actionMode.title =
                resources.getQuantityString(R.plurals.selectedCameras, selectedCameras.size, selectedCameras.size)

            val allFav = selectedCameras.all { it.isFavourite }
            addFav.isVisible = !allFav
            removeFav.isVisible = allFav

            val allInvis = selectedCameras.all { !it.isVisible }
            hide.isVisible = !allInvis
            unhide.isVisible = allInvis

            selectAll.isVisible = selectedCameras.size < listView.adapter.count
        }
        return camera in selectedCameras
    }

    override fun onItemCheckedStateChanged(mode: ActionMode?, position: Int, id: Long, checked: Boolean) {
        val adapter = listView.adapter as ArrayAdapter<*>
        if (!selectCamera(adapter.getItem(position) as Camera) && checked) {
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
