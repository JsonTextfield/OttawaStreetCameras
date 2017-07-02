package com.textfield.json.ottawastreetcameras;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Camera> cameras = new ArrayList<>();
    CameraAdapter cameraAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraAdapter = new CameraAdapter();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
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

        /*try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject g = (JSONObject) jsonArray.get(i);
                cameras.add(new Camera(g));
            }


        } catch (JSONException e) {
            //e.printStackTrace();
        }*/

        final ListView listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(cameraAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle b = new Bundle();
                b.putParcelable("camera", (Camera) listView.getAdapter().getItem(i));


                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        //doSomething();

    }

    private class CameraAdapter extends ArrayAdapter<Camera> {
        private ArrayList<Camera> data;
        private ArrayList<Camera> wholeList;

        private class ViewHolder {
            public TextView title;
        }

        public CameraAdapter() {
            super(MainActivity.this, 0, cameras);
            this.data = wholeList = cameras;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

                viewHolder.title = (TextView) convertView.findViewById(R.id.listtitle);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(data.get(position).getName());

            return convertView;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Nullable
        @Override
        public Camera getItem(int position) {
            return data.get(position);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    //Log.d(Constants.TAG, "**** PUBLISHING RESULTS for: " + constraint);
                    data = (ArrayList<Camera>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                    ArrayList<Camera> filteredResults = new ArrayList<>();

                    for (Camera s : wholeList) {
                        if (s.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredResults.add(s);
                        }
                    }

                    //System.out.print(filteredResults);

                    FilterResults results = new FilterResults();
                    results.values = filteredResults;

                    return results;
                }
            };
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.user_searchView));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    cameraAdapter.getFilter().filter("");
                    //listView.clearTextFilter();
                } else {
                    cameraAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        return true;
    }

}
