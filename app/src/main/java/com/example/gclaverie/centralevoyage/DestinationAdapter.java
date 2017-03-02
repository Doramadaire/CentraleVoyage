package com.example.gclaverie.centralevoyage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.ArrayList;

import static com.example.gclaverie.centralevoyage.VoyageSingleton.getInstance;

public class DestinationAdapter extends BaseAdapter {

    private static final String TAG = DestinationAdapter.class.getSimpleName();

    ArrayList<HashMap<String, String>> destination_list;
    // LayoutInflater aura pour mission de charger notre fichier XMLLayoutInflater inflater;
    LayoutInflater inflater;

    /**
     * Elle nous servira à mémoriser les éléments de la liste en mémoire pour
     * qu’à chaque rafraichissement l’écran ne scintille pas
     *
     * @author patrice
     */
    private class ViewHolder {
        TextView tvType;
        TextView tvDescription;
        ImageView tvImage;
    }

    public DestinationAdapter(Context context, ArrayList<HashMap<String, String>> objects) {
        inflater = LayoutInflater.from(context);
        this.destination_list = objects;
        Log.d(TAG, "DEST-ADAPT moi aussi j'ai recupere ma destination list, elle a pour taille=" + String.valueOf(destination_list.size()));
        Log.d(TAG, "ma list a pour clefs=" + String.valueOf(destination_list.size()));
    }

    /**
     * Génère la vue pour un objet
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView de la position=" + String.valueOf(position));
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.destination_item , null);
            holder.tvType = (TextView) convertView.findViewById(R.id.title);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.description);
            holder.tvImage = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, String> destination = destination_list.get(position);
        /*
        Log.d(TAG, "getView recupere une destination : " + destination.toString());
        Log.d(TAG, "getView dans dest list il y a desormais cb elems? "+ String.valueOf(destination_list.size()));
        destination_list.trimToSize();
        Log.d(TAG, "getView apres trim - desormais cb elems? "+ String.valueOf(destination_list.size()));
        Log.d(TAG, destination_list.toArray()[0].toString());
        Log.d(TAG, String.valueOf(destination_list.indexOf(destination_list.toArray()[0])));
        int count = 0;
        for (HashMap<String, String> dest: destination_list) {
            Log.d(TAG, "elem numero=" + String.valueOf(count));
            Log.d(TAG, dest.toString());
            count = count + 1;
        }*/
        holder.tvType.setText(destination.get("type"));
        holder.tvDescription.setText(destination.get("display"));
        Log.d(TAG, "type=" + destination.get("type"));
        Log.d(TAG, "display=" + destination.get("display"));
        //Pour l'image, on la récupère depuis la HashMap de notre singleton
        String img_url = destination.get("img_url");
        Bitmap image =  getInstance().getImagesMap().get(img_url);
        holder.tvImage.setImageBitmap(image);
        return convertView;
    }

    @Override
    public int getCount() {
        return destination_list.size();
    }

    /**
     * Retourne l'item à la position
     */@Override
    public HashMap<String, String> getItem(int position) {
        return destination_list.get(position);
    }

    /**
     * Retourne la position de l'item
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
}
