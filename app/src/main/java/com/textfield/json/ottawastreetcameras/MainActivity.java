package com.textfield.json.ottawastreetcameras;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ArrayList<Camera> cameras = new ArrayList<>();
    File cameraJson;
    CameraAdapter myAdapter;
    ListView cameraListView;
    private ArrayList<Camera> selectedCameras = new ArrayList<>();

    public void downloadJson() {

        String url = "http://traffic.ottawa.ca/map/camera_list";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    FileWriter fileWriter = new FileWriter(cameraJson);
                    fileWriter.write(response.toString());
                    fileWriter.flush();
                    setup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsObjRequest);

    }

    public void setup() {
        cameraListView = (ListView) findViewById(R.id.listView);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraListView.smoothScrollToPosition(0);
            }
        });
        setSupportActionBar(toolbar);

        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromFile());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject g = (JSONObject) jsonArray.get(i);
                cameras.add(new Camera(g));
            }


            Collections.sort(cameras);
            myAdapter = new CameraAdapter(this, cameras);
            cameraListView.setAdapter(myAdapter);

            final ArrayList<String> indexTitles = myAdapter.getIndexTitles();
            final HashMap<String, Integer> index = myAdapter.getIndex();

            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.indexHolder);
            for (int i = 0; i < index.size(); i++) {
                TextView t = new TextView(this);
                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

                t.setLayoutParams(layoutParams);
                t.setText(indexTitles.get(i));
                t.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                t.setGravity(Gravity.CENTER_HORIZONTAL);
                t.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

                final int finalI = i;
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cameraListView.setSelection(index.get(indexTitles.get(finalI)));
                    }
                });
                linearLayout.addView(t);
            }


            cameraListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle b = new Bundle();
                    ArrayList<Camera> cams = new ArrayList<>(Arrays.asList(new Camera[]{myAdapter.getItem(i)}));
                    b.putParcelableArrayList("cameras", cams);

                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
            cameraListView.setItemsCanFocus(true);

            cameraListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {
                    //cameraListView.getSelectedView().setSelected(b);
                    if (b) {
                        selectedCameras.add(myAdapter.getItem(i));
                    } else {
                        selectedCameras.remove(myAdapter.getItem(i));
                    }
                }

                @Override
                public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                    actionMode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.open_cameras) {
                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        intent.putParcelableArrayListExtra("cameras", selectedCameras);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(android.view.ActionMode actionMode) {
                    selectedCameras.clear();
                }
            });

        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraJson = new File(getCacheDir() + "/cameras.json");
        if (!cameraJson.exists()) {
            Log.w("FILE:", "Does not exist");
            downloadJson();
        } else {
            setup();
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

    public String loadJSONFromFile() {
        String json;
        try {
            InputStream is = new FileInputStream(cameraJson);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            //ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuItemMap) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putParcelableArrayListExtra("cameras", cameras);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.user_searchView));
        searchView.setQueryHint(String.format(getResources().getString(R.string.search_hint), cameras.size()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    myAdapter.getFilter().filter("");
                    cameraListView.clearTextFilter();
                } else {
                    myAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        return true;
    }
}
