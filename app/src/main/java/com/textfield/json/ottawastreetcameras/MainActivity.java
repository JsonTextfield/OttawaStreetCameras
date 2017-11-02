package com.textfield.json.ottawastreetcameras;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActionMode mActionMode;
    ArrayList<Camera> cameras = new ArrayList<>();

    CameraAdapter myAdapter;
    ListView cameraListView;
    private ArrayList<Camera> selectedCameras = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File cameraJson = new File(getCacheDir() + "/cameras.json");
        if (!cameraJson.exists()) {
            Log.w("FILE:", "Does not exist");
            //download the file
        }

        cameraListView = (ListView) findViewById(R.id.listView);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraListView.smoothScrollToPosition(0);
            }
        });
        setSupportActionBar(toolbar);

        DB myDB = new DB(this);
        myDB.createDatabase();
        myDB.open();
        Cursor cursor = myDB.runQuery("select * from cameras;");

        do {
            Camera camera = new Camera(cursor);
            cameras.add(camera);
        } while (cursor.moveToNext());
        myDB.close();

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
            t.setTextColor(Color.BLUE);
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
                selectedCameras.clear();
                selectedCameras.add(myAdapter.getItem(i));
                b.putParcelableArrayList("cameras", selectedCameras);

                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        cameraListView.setItemsCanFocus(true);
        /*cameraListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mActionMode != null) {
                    return false;
                }
                mActionMode = view.startActionMode(new ActionBarCallBack());
                view.setSelected(true);
                return true;

            }
        });*/

        cameraListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {
                if (b) {
                    selectedCameras.add(cameras.get(i));
                } else {
                    selectedCameras.remove(cameras.get(i));
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

        /*try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject g = (JSONObject) jsonArray.get(i);
                cameras.add(new Camera(g));
            }


        } catch (JSONException e) {
            //e.printStackTrace();
        }*/


    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("camera_list.json");
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

    /*private class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.open_cameras) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putParcelableArrayListExtra("cameras", selectedCameras);
                startActivity(intent);

            }
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {


        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

    }*/
}
