package com.example.gclaverie.centralevoyage;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.util.HashMap;
import java.util.ArrayList;

public final class VoyageSingleton extends Application {

    private static VoyageSingleton instance = null;

    private static final String TAG = VoyageSingleton.class.getSimpleName();

    private ArrayList<HashMap<String, String>> destinationList = new ArrayList<HashMap<String, String>>();
    private HashMap<String, Bitmap> imagesMap = new HashMap<String, Bitmap>();
    private String offset;

    public static VoyageSingleton getInstance()
    {
        if (VoyageSingleton.instance == null) {
            // synchronized sur ce bloc empêche toute instanciation multiple
            synchronized(VoyageSingleton.class) {
                if (VoyageSingleton.instance == null) {
                    VoyageSingleton.instance = new VoyageSingleton();
                }
            }
        }
        return VoyageSingleton.instance;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public ArrayList<HashMap<String, String>> getDestinationList() {
        return destinationList;
    }

    public HashMap<String, Bitmap> getImagesMap() {
        return imagesMap;
    }

    public void setDestinationList(ArrayList<HashMap<String, String>> destinationList) {
        this.destinationList = destinationList;
    }

    public void addDestination(HashMap<String, String> destination) {
        if (destination.containsKey("media_url")) {
            String img_url = destination.get("media_url");
            Bitmap bmImage = null;
            new DownloadImageTask(img_url, bmImage).execute();
            destination.put("img_url", img_url);
        }
        this.destinationList.add(destination);
        int count = 0;
        for (HashMap<String, String> dest: destinationList) {
            Log.d(TAG, "elem numero=" + String.valueOf(count));
            Log.d(TAG, dest.toString());
            count = count + 1;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Boolean> {
        Bitmap bmImg;
        String URL;

        public DownloadImageTask(String url, Bitmap bmImage) {
            this.URL = url;
            this.bmImg = bmImage;
        }

        protected Boolean doInBackground(String... urls) {
            try {
                //Log.d(TAG, "starts downloading image url="+URL);
                InputStream in = new java.net.URL(URL).openStream();
                bmImg = BitmapFactory.decodeStream(in);
                return true;
            } catch (Exception e) {
                Log.d(TAG, "encore un fail :/");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success)
        {
            super.onPostExecute(success);
            if (success) {
                imagesMap.put(URL, bmImg);
            }
        }
    }
}
