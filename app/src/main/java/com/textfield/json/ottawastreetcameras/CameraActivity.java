package com.textfield.json.ottawastreetcameras;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class CameraActivity extends AppCompatActivity {

    private static boolean RUNNING;
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RUNNING = true;
        setContentView(R.layout.activity_camera);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.camera_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        Bundle bundle = getIntent().getExtras();
        camera = new Camera(bundle.getString("name"), bundle.getString("id"));
        getSupportActionBar().setTitle(camera.getName());

        getSessionId();
    }

    public void getSessionId() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL oracle = new URL("http://traffic.ottawa.ca/map");
                    oracle.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void b) {
                new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute("http://traffic.ottawa.ca/map/camera?id=" + camera.getId());
            }
        }.execute();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView imageView) {
            bmImage = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }

            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                URL myUrl = new URL(urldisplay);
                URLConnection urlConnection = myUrl.openConnection();
                //urlConnection.setRequestProperty("Cookie", MainActivity.SESSION_ID);
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
            }
            Log.w("STREETCAM", "updated");
            if (RUNNING) {
                new DownloadImageTask(bmImage).execute("http://traffic.ottawa.ca/map/camera?id=" + camera.getId());
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
