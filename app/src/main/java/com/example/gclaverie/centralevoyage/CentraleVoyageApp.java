package com.example.gclaverie.centralevoyage;

import android.app.Application;
import java.util.HashMap;
import java.util.ArrayList;

public class CentraleVoyageApp extends Application {

    private ArrayList<HashMap<String, String>> destinationList;

    public ArrayList<HashMap<String, String>> getDestinationList() {
        return destinationList;
    }

    public void setDestinationList(ArrayList<HashMap<String, String>> destinationList) {
        this.destinationList = destinationList;
    }

    public void addDestination(HashMap<String, String> destination) {
        this.destinationList.add(destination);
    }
}
