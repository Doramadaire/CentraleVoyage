package com.example.gclaverie.centralevoyage;

import android.app.ListActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;
import android.widget.Toast;

import static com.example.gclaverie.centralevoyage.VoyageSingleton.getInstance;

public class DisplayDestinations extends ListActivity {

    private static final String TAG = DisplayDestinations.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_display_destinations);

        //Création de la ArrayList qui nous permettra d'alimenter la listView
        ArrayList<HashMap<String, String>> listItem = getInstance().getDestinationList();

        //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.destination_list,
                new String[] {"img", "type", "display"}, new int[] {R.id.img, R.id.title, R.id.description});
        //TO DO : changer adapter pour qu'il prenne mon image

        //On attribut à notre listActivity l'adapter que l'on vient de créer
        setListAdapter(mSchedule);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //Récupèrer la Map qui contient les informations de l'item (titre, decription et image)
        HashMap<String, String> map = (HashMap<String, String>) getListAdapter().getItem(position);
        Toast.makeText(this, map.get("display")+ " selected", Toast.LENGTH_LONG).show();
    }
}
