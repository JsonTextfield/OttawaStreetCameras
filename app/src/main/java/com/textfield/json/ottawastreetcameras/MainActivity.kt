package com.textfield.json.ottawastreetcameras

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
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

class MainActivity : AppCompatActivity() {
    internal var cameras = ArrayList<Camera>()
    internal lateinit var cameraJson: File
    internal lateinit var myAdapter: CameraAdapter
    internal lateinit var cameraListView: ListView
    private val selectedCameras = ArrayList<Camera>()

    fun downloadJson() {

        val url = "http://traffic.ottawa.ca/map/camera_list"
        val queue = Volley.newRequestQueue(this)
        val jsObjRequest = JsonArrayRequest(url, Response.Listener { response ->
            try {
                val fileWriter = FileWriter(cameraJson)
                fileWriter.write(response.toString())
                fileWriter.flush()
                setup()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { })
        queue.add(jsObjRequest)

    }

    fun setup() {

        cameraListView = listView

        main_toolbar.setOnClickListener { cameraListView.smoothScrollToPosition(0) }
        setSupportActionBar(main_toolbar)

        try {
            val jsonArray = JSONArray(loadJSONFromFile())
            for (i in 0 until jsonArray.length()) {
                val g = jsonArray.get(i) as JSONObject
                cameras.add(Camera(g))
            }


            Collections.sort(cameras)
            myAdapter = CameraAdapter(this, cameras)
            cameraListView.adapter = myAdapter

            val indexTitles = myAdapter.getIndexTitles()
            val index = myAdapter.getIndex()

            val linearLayout = findViewById(R.id.indexHolder) as LinearLayout?
            for (i in 0 until index.size) {
                val t = TextView(this)
                val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL

                t.layoutParams = layoutParams
                t.text = indexTitles[i]
                t.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                t.gravity = Gravity.CENTER_HORIZONTAL
                t.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))

                t.setOnClickListener { cameraListView.setSelection(index[indexTitles[i]]!!) }
                linearLayout!!.addView(t)
            }


            cameraListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val b = Bundle()
                val cams = ArrayList(Arrays.asList(myAdapter.getItem(i)!!))
                b.putParcelableArrayList("cameras", cams)

                val intent = Intent(this@MainActivity, CameraActivity::class.java)
                intent.putExtras(b)
                startActivity(intent)
            }
            cameraListView.itemsCanFocus = true

            cameraListView.setMultiChoiceModeListener(object : AbsListView.MultiChoiceModeListener {
                override fun onItemCheckedStateChanged(actionMode: android.view.ActionMode, i: Int, l: Long, b: Boolean) {
                    //cameraListView.getSelectedView().setSelected(b);
                    if (b) {
                        selectedCameras.add(myAdapter.getItem(i)!!)
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

        } catch (e: JSONException) {
            //e.printStackTrace();
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraJson = File(cacheDir.toString() + "/cameras.json")
        if (!cameraJson.exists()) {
            Log.w("FILE:", "Does not exist")
            downloadJson()
        } else {
            setup()
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

    fun loadJSONFromFile(): String? {
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
        if (item.itemId == R.id.menuItemMap) {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putParcelableArrayListExtra("cameras", cameras)
            startActivity(intent)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchView = MenuItemCompat.getActionView(menu.findItem(R.id.user_searchView)) as SearchView
        searchView.queryHint = String.format(resources.getString(R.string.search_hint), cameras.size)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    myAdapter.filter.filter("")
                    cameraListView.clearTextFilter()
                } else {
                    myAdapter.filter.filter(newText)
                }
                return true
            }
        })

        return true
    }
}
