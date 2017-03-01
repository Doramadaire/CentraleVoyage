package com.example.gclaverie.centralevoyage;

import android.app.ListActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.gclaverie.centralevoyage.VoyageSingleton.getInstance;

public class DisplayDestinations extends ListActivity {

    private static final String TAG = DisplayDestinations.class.getSimpleName();

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

        /*
        // Gestion des clics sur listview.
        myListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type = ((TextView) view.findViewById(R.id.type)).getText().toString();
                String idIntent = ((TextView) view.findViewById(R.id.id)).getText().toString();
                // Reste Parcours et destination a faire.
                if (type.equals("POI")) {
                    Intent adapterPOI = new Intent(MainActivity.this, POI_Adapter.class);
                    adapterPOI.putExtra("id", idIntent);
                    startActivity(adapterPOI);
                }
                if (type.equals("PARCOURS")) {
                    Intent adapterPARCOURS = new Intent(MainActivity.this, PARCOURS_Adapter.class);
                    adapterPARCOURS.putExtra("id", idIntent);
                    startActivity(adapterPARCOURS);
                }
                if (type.equals("ADMIN") || type.equals("CITY")) {
                    Intent DESTINATION_Adapter = new Intent(MainActivity.this, DESTINATION_Adapter.class);
                    DESTINATION_Adapter.putExtra("id", idIntent);
                    startActivity(DESTINATION_Adapter);
                }
            }
        });*/
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //Récupèrer la Map qui contient les informations de l'item (titre, decription et image)
        HashMap<String, String> map = (HashMap<String, String>) getListAdapter().getItem(position);
        Log.v(TAG, map.get("display"));
        //Toast.makeText(this, map.get("display")+ " selected", Toast.LENGTH_LONG).show();
    }
}
