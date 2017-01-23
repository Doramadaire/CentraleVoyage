package com.example.gclaverie.centralevoyage;

import android.app.Activity;
import android.app.ProgressDialog;
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
import java.util.Map;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("lat", "43.14554197717751");
        parameters.put("lon", "6.00246207789145");
        parameters.put("offset", "0");
        final String apiUrl = "http://voyage2.corellis.eu/api/v2/homev2?";

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
        private ProgressDialog pDialog;

        public DownloadStreamTask(String url, HashMap<String, String> params){
            this.URL = url;
            this.parameters = params;
        }

        @Override
        protected String doInBackground(String... params)
        {
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
                    Log.d(TAG, URL);
                }
                URL apiURL = new URL(URL);
                URLConnection urlConnection = apiURL.openConnection();
                int lenghtOfFile = urlConnection.getContentLength();

                //reading the response from the urlconnection via a bufferedreader
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    content.append(line + "\n");
                }
                bufferedReader.close();
                return content.toString();
            }
                catch (Exception e) {}
            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            TextView txt = (TextView) findViewById(R.id.output);
            txt.setText("Response is: "+ result.substring(0,500));
//
            try {
                JSONObject theObject = new JSONObject(result);
                txt.setText("Response is: "+theObject.getString("status")+"\n"+
                        theObject.getString("count")+"/"+theObject.getString("count_total"));
            } catch (Exception e){
                txt.setText("Error during process");
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getApplicationContext());
            pDialog.setMessage("Reqûete en cours de traitement");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }



}
