package com.example.gclaverie.centralevoyage;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected ProgressBar pBar;
    protected Location location;
    private TextView textError;
    private static final int LOCATION_PERMISSION = 307;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textError = (TextView) findViewById(R.id.textError);
        textError.setText("");

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "si on a pas la permission on va la demander");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.d(TAG, "dans le if pour expliquer");

            } else {
                Log.d(TAG, "HORS du dans le if pour expliquer");
                // No explanation needed, we can request the permission.

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "pas besoin de demander la permission, on l'a déjà");
        }

        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("lat", "43.14554197717751");
        parameters.put("lon", "6.00246207789145");
        parameters.put("offset", "0");
        final String apiUrl = "http://voyage2.corellis.eu/api/v2/homev2?";

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.GONE);

        Button button1 = (Button) findViewById(R.id.start_asynctask);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DownloadStreamTask(apiUrl, parameters).execute();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "ouais permission accordée");

                    // permission was granted, yay!

                } else {
                    Log.d(TAG, "permissions refusée");
                    textError.setText("L'application a besoin des données de géolocalisation pour fonctionner");
                    // permission denied, boo!
                    // Disable the functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private class DownloadStreamTask extends AsyncTask<String, Void, String> {

        String URL;
        HashMap<String, String> parameters;

        private TextView progressText;

        public DownloadStreamTask(String url, HashMap<String, String> params){
            this.URL = url;
            this.parameters = params;
        }

        @Override
        protected String doInBackground(String... params)
        {
            Log.d(TAG, "retriving data from API");
            StringBuilder content = new StringBuilder();
            try
            {
                if (parameters != null)
                {
                    for(Map.Entry<String, String> entry : parameters.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        URL += "&"+key+"="+value;
                    }
                    Log.d(TAG, "url="+URL);
                }
                URL apiURL = new URL(URL);
                URLConnection urlConnection = apiURL.openConnection();
                try {
                    urlConnection.connect();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                //reading the response from the urlconnection via a bufferedreader
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                    content.append(line + "\n");
                    //Log.d(TAG, "data="+line);
                }
                bufferedReader.close();
                return content.toString();
            }
                catch (Exception e) {Log.d(TAG, e.toString());}
            Log.d(TAG, "echec de la tâche asynchrone");
            return "";

        }

        @Override
        protected void onPostExecute(String result)
        {
            pBar.setVisibility(View.GONE);

            try {
                progressText.setText("Tâche finie et réussie");
                JSONObject theObject = new JSONObject(result);
            } catch (Exception e){
                progressText.setText("Tâche finie et ratée");
            }

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            pBar.setVisibility(View.VISIBLE);
            progressText = (TextView) findViewById(R.id.progressText);
            progressText.setText("Tâche en cours");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
}
