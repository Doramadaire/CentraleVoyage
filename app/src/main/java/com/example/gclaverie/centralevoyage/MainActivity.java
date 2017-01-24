package com.example.gclaverie.centralevoyage;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
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

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected ProgressBar pBar;
    protected Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = getLastKnownLocation();


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
