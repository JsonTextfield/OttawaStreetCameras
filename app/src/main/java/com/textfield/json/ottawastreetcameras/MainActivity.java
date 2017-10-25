package com.textfield.json.ottawastreetcameras;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ArrayList<Camera> cameras = new ArrayList<>();
    MyAdapter myAdapter;
    FloatingGroupExpandableListView myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myList = (FloatingGroupExpandableListView) findViewById(R.id.listView);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList.smoothScrollToPosition(0);
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


        HashMap<String, List<Camera>> data = new HashMap<>();
        for (Camera camera : cameras) {
            String c = "";
            if (Locale.getDefault().getDisplayLanguage().equals("fr")) {
                c = Character.toString(camera.getNameFr().replaceAll("\\W", "").charAt(0));
            } else {
                c = Character.toString(camera.getName().replaceAll("\\W", "").charAt(0));
            }
            if (!data.keySet().contains(c)) {
                data.put(c, new ArrayList<Camera>());
            }
            data.get(c).add(camera);
        }
        ArrayList<String> list = new ArrayList<>(data.keySet());
        Collections.sort(list);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.indexHolder);
        for (int i = 0; i < list.size(); i++) {
            TextView t = new TextView(this);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            t.setLayoutParams(layoutParams);
            t.setText(list.get(i));
            t.setTextColor(Color.BLUE);
            t.setGravity(Gravity.CENTER_HORIZONTAL);
            t.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            final int finalI = i;
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myList.setSelectedGroup(finalI);
                }
            });
            linearLayout.addView(t);
        }

        myAdapter = new MyAdapter(MainActivity.this, list, data);
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(myAdapter);
        myList.setAdapter(wrapperAdapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle b = new Bundle();
                b.putParcelable("camera", (Camera) myList.getAdapter().getItem(i));

                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtras(b);
                startActivity(intent);
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
                    myList.clearTextFilter();
                } else {
                    myAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        return true;
    }

}
