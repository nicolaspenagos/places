package com.example.places;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.places.model.Place;
import com.example.places.util.UtilImage;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceView> {

    private ArrayList<Place> places;

    public PlaceAdapter(){
        places = new ArrayList<>();
    }

    public void addPlace(Place place){

        places.add(place);
        this.notifyDataSetChanged();

    }

    @NonNull
    @Override
    public PlaceView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View row = inflater.inflate(R.layout.placerow, parent, false);
        ConstraintLayout rowRoot = (ConstraintLayout) row;
        PlaceView placeView = new PlaceView(rowRoot);


        return placeView;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceView holder, int position) {

        Place place = places.get(position);

        holder.getPlaceNameTextView().setText(place.getName());
        holder.getPlacePicImageView().setImageBitmap(UtilImage.createImageFromPath(place.getPath()));
        holder.getStarsTextView().setText("5");

    }

    @Override
    public int getItemCount() {
        return places.size();
    }
}
