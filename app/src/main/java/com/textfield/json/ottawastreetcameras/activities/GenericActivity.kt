package com.textfield.json.ottawastreetcameras.activities

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

abstract class GenericActivity : AppCompatActivity() {

    fun modifyPrefs(pref: String, selectedCameras: Collection<Camera>, willAdd: Boolean) {
        val sharedPrefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        val prefList = HashSet<String>(sharedPrefs.getStringSet(pref, HashSet<String>()))
        val editor = sharedPrefs.edit()

        if (willAdd) {
            prefList.addAll(selectedCameras.map { it.num.toString() })
        } else {
            prefList.removeAll(selectedCameras.map { it.num.toString() })
        }
        editor.putStringSet(pref, prefList).apply()
    }

    fun showErrorDialogue(context: Context) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle(resources.getString(R.string.no_network_title))
                .setMessage(resources.getString(R.string.no_network_content))
                .setPositiveButton("OK") { _, _ -> finish() }
                .setOnDismissListener { finish() }
        val dialog = builder.create()
        dialog.show()
    }
}
