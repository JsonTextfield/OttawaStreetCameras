package com.textfield.json.ottawastreetcameras.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.LinearLayout.LayoutParams
import android.widget.ListView
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.textfield.json.ottawastreetcameras.Camera
import com.textfield.json.ottawastreetcameras.SortByDistance
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SortByName
import com.textfield.json.ottawastreetcameras.adapters.CameraAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var cameras = ArrayList<Camera>()
    private val selectedCameras = ArrayList<Camera>()

    internal lateinit var myAdapter: CameraAdapter

    private val maxCameras = 3

    fun downloadJson(cameraJson: File) {

        val url = "http://traffic.ottawa.ca/map/camera_list"
        val queue = Volley.newRequestQueue(this)
        val jsObjRequest = JsonArrayRequest(url, Response.Listener { response ->
            try {
                val fileWriter = FileWriter(cameraJson)
                fileWriter.write(response.toString())
                fileWriter.flush()
                setup(cameraJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            val builder = AlertDialog.Builder(this@MainActivity)

            builder.setTitle(resources.getString(R.string.no_network_title)).setMessage(resources.getString(R.string.no_network_content))
                    .setPositiveButton("OK") { _, _ -> finish() }
            val dialog = builder.create()
            dialog.show()
        })
        queue.add(jsObjRequest)

    }

    fun setupSectionIndex() {
        val index = HashSet<Char>()

        //assumes cameras are sorted
        for (i in 0 until cameras.size) {
            val camera = cameras[i]

            //get the first character
            val c = camera.getName().replace("\\W".toRegex(), "")[0]

            if (!index.contains(c)) {
                index.add(c)
                val t = TextView(this)
                val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL

                t.layoutParams = layoutParams
                t.text = c.toString()
                t.textSize = 10f
                t.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                t.gravity = Gravity.CENTER_HORIZONTAL
                t.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
                t.setOnClickListener { listView.setSelection(i) }

                indexHolder.addView(t)
            }

        }
    }

    fun setupListView() {
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val b = Bundle()
            val cams = ArrayList(Arrays.asList(myAdapter.getItem(i)!!))
            b.putParcelableArrayList("cameras", cams)

            val intent = Intent(this@MainActivity, CameraActivity::class.java)
            intent.putExtras(b)
            startActivity(intent)
        }
        listView.itemsCanFocus = true

        listView.setMultiChoiceModeListener(object : AbsListView.MultiChoiceModeListener {
            override fun onItemCheckedStateChanged(actionMode: android.view.ActionMode, i: Int, l: Long, b: Boolean) {
                if (b) {
                    if (selectedCameras.size < maxCameras) {
                        selectedCameras.add(myAdapter.getItem(i)!!)
                    } else {
                        selectedCameras.remove(myAdapter.getItem(i))
                    }
                } else {
                    selectedCameras.remove(myAdapter.getItem(i))
                }
            }

            override fun onCreateActionMode(actionMode: android.view.ActionMode, menu: Menu): Boolean {
                actionMode.menuInflater.inflate(R.menu.contextual_menu, menu)
                return true
            }

            override fun onPrepareActionMode(actionMode: android.view.ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(actionMode: android.view.ActionMode, menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.open_cameras) {
                    val intent = Intent(this@MainActivity, CameraActivity::class.java)
                    intent.putParcelableArrayListExtra("cameras", selectedCameras)
                    startActivity(intent)
                    return true
                }
                return false
            }

            override fun onDestroyActionMode(actionMode: android.view.ActionMode) {
                selectedCameras.clear()
            }
        })
    }

    fun setup(cameraJson: File) {
        main_toolbar.setOnClickListener { listView.smoothScrollToPosition(0) }
        setSupportActionBar(main_toolbar)

        try {
            val jsonArray = JSONArray(loadJSONFromFile(cameraJson))
            (0 until jsonArray.length())
                    .map { jsonArray.get(it) as JSONObject }
                    .forEach { cameras.add(Camera(it)) }


            Collections.sort(cameras, SortByName())
            myAdapter = CameraAdapter(this, cameras)
            listView.adapter = myAdapter

            setupSectionIndex()
            setupListView()

        } catch (e: JSONException) {
            //e.printStackTrace();
        }

    }

    fun getViewByPosition(pos: Int, listView: ListView): View {
        val firstListItemPosition = listView.firstVisiblePosition
        val lastListItemPosition = firstListItemPosition + listView.childCount - 1

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.adapter.getView(pos, null, listView)
        } else {
            val childIndex = pos - firstListItemPosition
            return listView.getChildAt(childIndex)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cameraJson = File(cacheDir.toString() + "/cameras.json")
        if (!cameraJson.exists()) {
            Log.w("FILE:", "Does not exist")
            downloadJson(cameraJson)
        } else {
            setup(cameraJson)
        }


        /*DB myDB = new DB(this);
        myDB.createDatabase();
        myDB.open();
        Cursor cursor = myDB.runQuery("select * from cameras;");

        do {
            Camera camera = new Camera(cursor);
            cameras.add(camera);
        } while (cursor.moveToNext());
        myDB.close();

        Collections.sort(cameras);*/


        /*try {
            JSONArray jsonArray = new JSONArray(loadJSONFromFile());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject g = (JSONObject) jsonArray.get(i);
                cameras.add(new Camera(g));
            }


        } catch (JSONException e) {
            //e.printStackTrace();
        }*/


    }

    fun loadJSONFromFile(cameraJson: File): String? {
        val json: String
        try {
            val `is` = FileInputStream(cameraJson)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            //ex.printStackTrace();
            return null
        }

        return json
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItemMap -> {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putParcelableArrayListExtra("cameras", cameras)
                startActivity(intent)
            }
            R.id.sort_name -> {
                myAdapter.sort(SortByName())
                indexHolder.visibility = View.VISIBLE
            }
            R.id.distance_sort -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            0)
                    // Permission is not granted
                } else {
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    myAdapter.sort(SortByDistance(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)))
                    indexHolder.visibility = View.INVISIBLE
                }
            }
        }
        if (item.itemId == R.id.menuItemMap) {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putParcelableArrayListExtra("cameras", cameras)
            startActivity(intent)
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    myAdapter.sort(SortByDistance(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)))

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.

            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchView = menu.findItem(R.id.user_searchView).actionView as SearchView
        searchView.queryHint = String.format(resources.getString(R.string.search_hint), cameras.size)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    myAdapter.filter.filter("")
                    listView.clearTextFilter()
                    indexHolder.visibility = View.VISIBLE
                } else {
                    myAdapter.filter.filter(newText)
                    indexHolder.visibility = View.INVISIBLE
                }
                return true
            }
        })

        return true
    }
}
