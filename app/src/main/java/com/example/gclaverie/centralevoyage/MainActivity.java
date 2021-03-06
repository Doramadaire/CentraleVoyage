package com.example.gclaverie.centralevoyage;

import android.Manifest;
import android.content.Intent;
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
import org.json.JSONArray;

import com.squareup.picasso.Picasso;

import static com.example.gclaverie.centralevoyage.VoyageSingleton.getInstance;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION = 1057;

    protected ProgressBar pBar;
    protected Button ask_permission_button;
    protected Button retry_button;
    protected Button goToDestButton;
    private TextView textError;
    private TextView progressText;

    private HashMap<String, String> parameters;
    private final String apiUrl = "http://voyage2.corellis.eu/api/v2/homev2?";
    protected Location location;
    protected LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parameters = new HashMap<String, String>();

        textError = (TextView) findViewById(R.id.textError);
        textError.setText("");

        progressText = (TextView) findViewById(R.id.progressText);
        progressText.setText("");

        ask_permission_button = (Button) findViewById(R.id.ask_permission_button);
        ask_permission_button.setVisibility(View.GONE);
        ask_permission_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //lancer une demande de permissions
                ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            }
        });

        retry_button = (Button) findViewById(R.id.retry_button);
        retry_button.setVisibility(View.GONE);
        retry_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //réessayer de lancer le chargement du flux depuis l'API
                tryLoadingData();
            }
        });

        goToDestButton = (Button) findViewById(R.id.goToDestButton);
        goToDestButton.setVisibility(View.GONE);
        goToDestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "click (via intent) pour démarrer l'activité DisplayDestinations");
                Intent dispDestIntent = new Intent( MainActivity.this, DisplayDestinations.class );
                startActivity(dispDestIntent);
            }
        });

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.GONE);

        //On passe aux choses sérieuses
        tryLoadingData();
    }

    public void tryLoadingData() {
        //on vérifie l'accès aux données de localisation
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //si on n'a pas l'autorisation, on la demande
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        } else {
            //Permissions accordées : lancement du chargement
            loadData();
        }
    }

    public void loadData() {
        //on récupère la position
        //Log.d(TAG, "OUAIS JE VAIS RECUPERER MA POSITION");

        Location location = getLastKnownLocation();
        if (location == null){
            Log.d(TAG, "fais iech j'ai location=null");
            progressText.setText("Aucune position trouvée");
            retry_button.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "cool, location != null");
            //on récupére la position et on la met dans une hashmap
            parameters.put("lat", String.valueOf(location.getLatitude()));
            parameters.put("lon", String.valueOf(location.getLongitude()));
            //parameters.put("lat", "43.14554197717751");
            //parameters.put("lon", "6.00246207789145");
            parameters.put("offset", "0");
            //on envoie la requete asynchrone
            new DownloadStreamTask(apiUrl, parameters).execute();
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permissions accordées : lancement du chargement
                    tryLoadingData();
                } else {
                    //Permissions refusés, on peut rien faire à part redemander
                    textError.setText("L'application a besoin des données de géolocalisation pour fonctionner");
                    ask_permission_button.setVisibility(View.VISIBLE);
                }
                return;
            }
        }
    }

    private class DownloadStreamTask extends AsyncTask<String, Void, Boolean> {

        String URL;
        HashMap<String, String> parameters;

        private TextView progressText;

        public DownloadStreamTask(String url, HashMap<String, String> params){
            this.URL = url;
            this.parameters = params;
        }

        @Override
        protected Boolean doInBackground(String... params)
        //retourne true si tout s'est bien passé, false sinon
        {
            //On commence par télécharger le résultat de l'API
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
                    //Log.d(TAG, "url avec paramètres="+URL);
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
                //On a le résultat, maintenant on le parse et on le met dans une jolie hashmap
                //Log.d(TAG, content.toString());

                try {

                    JSONObject retrievedJSON = new JSONObject(content.toString());
                    JSONArray dataArray = retrievedJSON.getJSONArray("data");

                    getInstance().setOffset(String.valueOf(retrievedJSON.getInt("offset")));

                    for (int i = 0; i < dataArray.length(); i++)
                    {
                        HashMap<String, String> destination = new HashMap<String, String>();
                        try {
                            JSONObject currentObj = dataArray.getJSONObject(i);
                            String type = currentObj.getString("type");

                            destination.put("display", currentObj.getString("display"));
                            destination.put("media_url", currentObj.getString("media"));

                            Log.d(TAG, currentObj.getString("media"));

                            switch (type) {
                                case "CITY":
                                case "ADMIN":
                                    destination.put("type", "Ville");
                                    getInstance().addDestination(destination);
                                    break;

                                case "POI":
                                    destination.put("type", "POI");
                                    getInstance().addDestination(destination);
                                    break;

                                case "PARCOURS":
                                    destination.put("type", "Parcours");
                                    getInstance().addDestination(destination);
                                    break;

                                default:
                                    //on garde pas les autres types...
                                    break;
                            }
                        } catch (Exception e) {Log.d(TAG, "Error while parsing the following JSON object " + dataArray.getJSONObject(i).toString() + "\n" + e.toString());}
                    }
                } catch (Exception e){
                    Log.d(TAG, "on a pas réussis à parse le Json");
                    Log.d(TAG, e.toString());
                }
                //Log.d(TAG, "on retourne succès");
                return true;
            }
                catch (Exception e) {Log.d(TAG, e.toString());}
            Log.d(TAG, "Récupération du flux JSON échoué");
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success)
        {
            super.onPostExecute(success);
            if (success)  {
                //quand c'et chargé on va direct sur la nouvelle activité qui présente les contenus
                pBar.setVisibility(View.GONE);
                progressText.setText("Chargement terminé");
                goToDestButton.setVisibility(View.VISIBLE);
                Log.d(TAG, "lance nouvel intent pour démarrer l'activité DisplayDestinations");
                Intent dispDestIntent = new Intent( MainActivity.this, DisplayDestinations.class );
                startActivity(dispDestIntent);
            } else {
                //si échec bouton réessayer
                retry_button.setVisibility(View.VISIBLE);
                progressText.setText("Chargement échoué, veuillez réessayer");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pBar.setVisibility(View.VISIBLE);
            progressText = (TextView) findViewById(R.id.progressText);
            progressText.setText("Chargement en cours...");
        }
    }
}
