package com.example.gclaverie.centralevoyage;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.ArrayList;

public final class VoyageSingleton extends Application {

    private static VoyageSingleton instance = null;

    private static final String TAG = VoyageSingleton.class.getSimpleName();

    private ArrayList<HashMap<String, String>> destinationList = new ArrayList<HashMap<String, String>>();
    private HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();
    private String offset;

    /*
    private VoyageSingleton(){
        super();
    }*/

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

    public void setDestinationList(ArrayList<HashMap<String, String>> destinationList) {
        this.destinationList = destinationList;
    }

    public void addDestination(HashMap<String, String> destination) {
        if (destination.containsKey("media_url")) {
            String img_url = destination.get("media_url");
            Bitmap bmImage = null;
            new DownloadImageTask(img_url, bmImage).execute();
            destination.put("img", String.valueOf(bmImage));
            if (bmImage == null) {
                Log.d(TAG, "img nulle");
            } else {
                Log.d(TAG, "ouais, img pas nulle");
            }
            this.images.put(img_url, bmImage);
            //destination.put("img", images.get(img_url));
            //pb : adapter, lui expliquer
        }
        this.destinationList.add(destination);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bmImg;
        String URL;

        public DownloadImageTask(String url, Bitmap bmImage) {
            this.URL = url;
            this.bmImg = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            Bitmap mIcon11 = null;
            try {
                Log.d(TAG, "image url="+URL);
                InputStream in = new java.net.URL(URL).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.d(TAG, "encore un fail :/");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
    }
}
