package com.example.gclaverie.centralevoyage;

import android.app.ListActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;
import android.widget.Toast;

import static com.example.gclaverie.centralevoyage.VoyageSingleton.getInstance;

public class DisplayDestinations extends ListActivity {

    private static final String TAG = DisplayDestinations.class.getSimpleName();
    private static DestinationAdapter adapter = null;
    private static boolean isCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_destinations);

        //Création de la ArrayList qui nous permettra d'alimenter la listView
        ArrayList<HashMap<String, String>> listItem = getInstance().getDestinationList();

        ListView myListView = (ListView) findViewById(R.id.destinationListView);
        DestinationAdapter adapter = new DestinationAdapter(this, listItem);
        myListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        isCreated = true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //Récupèrer la Map qui contient les informations de l'item (titre, decription et image)
        HashMap<String, String> map = (HashMap<String, String>) getListAdapter().getItem(position);
        Toast.makeText(this, map.get("display")+ " selected", Toast.LENGTH_LONG).show();
    }

    public static boolean isCreated() {
        return isCreated;
    }

    public static void updateImage() {
        try {
            adapter.invalidateImage();
        } catch (Exception e) {
            Log.d(TAG, "encore un fail :/");
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }
}
