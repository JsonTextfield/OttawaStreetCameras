package com.textfield.json.ottawastreetcameras;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    private static boolean RUNNING;
    private static String SESSION_ID;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RUNNING = true;
        setContentView(R.layout.activity_camera);

        linearLayout = (LinearLayout) findViewById(R.id.layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.camera_toolbar);
        toolbar.setTitle("");
        TextView title = (TextView) toolbar.findViewById(R.id.textView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle bundle = getIntent().getExtras();
        List<Camera> cameras = bundle.getParcelableArrayList("cameras");
        if (cameras.size() > 2) {
            cameras = cameras.subList(0, 2);
        }
        StringBuilder allTitles = new StringBuilder();
        for (Camera camera : cameras) {
            if (Locale.getDefault().getDisplayLanguage().contains("fr"))
                allTitles.append(camera.getNameFr() + ", ");
            else
                allTitles.append(camera.getName() + ", ");
        }
        String s = allTitles.substring(0, allTitles.length() - 2);
        title.setText(s);
        new GetSessionIdTask(new ArrayList<Camera>(cameras)).execute();
    }

    private class GetSessionIdTask extends AsyncTask<Void, Void, Boolean> {

        ArrayList<Camera> cameras;

        public GetSessionIdTask(ArrayList<Camera> cameras) {
            this.cameras = cameras;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL oracle = new URL("http://traffic.ottawa.ca/map");
                HttpURLConnection urlConnection = (HttpURLConnection) oracle.openConnection();
                CameraActivity.SESSION_ID = urlConnection.getHeaderFields().get("Set-Cookie").get(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                RUNNING = false;
                return RUNNING;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (RUNNING) {
                for (Camera camera : cameras) {
                    ImageView imageView = new ImageView(CameraActivity.this);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT / cameras.size(), 1));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    linearLayout.addView(imageView);
                    new DownloadImageTask(imageView, camera).execute();
                }
            } else {
                findViewById(R.id.errortext).setVisibility(View.VISIBLE);
            }
        }
    }


    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
        ImageView bmImage;
        Camera camera;

        public DownloadImageTask(ImageView imageView, Camera camera) {
            bmImage = imageView;
            this.camera = camera;
        }

        protected Bitmap doInBackground(Void... v) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            String url = "http://traffic.ottawa.ca/map/camera?id=" + camera.getNum();
            Bitmap mIcon11 = null;
            try {
                URL myUrl = new URL(url);
                URLConnection urlConnection = myUrl.openConnection();
                urlConnection.setRequestProperty("Cookie", CameraActivity.SESSION_ID);
                //urlConnection.connect();
                InputStream in = urlConnection.getInputStream();

                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                //Log.e("Error", e.getMessage());
                //e.printStackTrace();
            }

            return mIcon11;

        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bmImage.setImageBitmap(result);
            } else {
                findViewById(R.id.errortext).setVisibility(View.VISIBLE);
            }
            //Log.w("STREETCAM", "updated");
            if (RUNNING) {
                new DownloadImageTask(bmImage, camera).execute();
            }
        }

    }

    @Override
    public void onBackPressed() {
        RUNNING = false;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        RUNNING = false;
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
