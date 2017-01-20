package com.example.gclaverie.centralevoyage;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;

public class MainActivity extends Activity {

    private ProgressBar myProgressBar;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myProgressBar = (ProgressBar) findViewById(R.id.aProgressBar);
    }

    private class DownloadStreamTask extends AsyncTask<String, Void, String> {

        String URL;
        HashMap<String, String> parameters;

        public DownloadStreamTask(String url, HashMap<String, String> params){
            this.URL = url;
            this.parameters = params;
        }

        @Override
        protected String doInBackground(String... params)
        {
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

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                /*
                String line;

                // read from the urlconnection via the bufferedreader
                while ((line = bufferedReader.readLine()) != null)
                {
                    content.append(line + "\n");
                }
                bufferedReader.close();
                return "a";
            }
                catch (Exception e) {}
            return "";*/
        }

        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalSize = 0;
            for (int i = 0; i < count; i++) {
                totalSize += Downloader.downloadFile(urls[i]);
                publishProgress((int) ((i / (float) count) * 100));
                // Escape early if cancel() is called
                if (isCancelled()) break;
            }
            return totalSize;
        }

        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
            showDialog("Downloaded " + result + " bytes");
        }
    }



}
