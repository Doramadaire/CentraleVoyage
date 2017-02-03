package com.example.gclaverie.centralevoyage;

import android.app.Application;
import java.util.HashMap;
import java.util.ArrayList;

public final class VoyageSingleton extends Application {

    private static VoyageSingleton instance = null;

    private ArrayList<HashMap<String, String>> destinationList = new ArrayList<HashMap<String, String>>();
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
        this.destinationList.add(destination);
    }
}
