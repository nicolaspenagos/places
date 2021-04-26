package com.example.places;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.places.interfaces.OnBottomNavigationBar;
import com.example.places.model.Place;
import com.example.places.util.UtilImage;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceView> {

    public void setOnGoToPlaceObserver(OnGoToPlace onGoToPlaceObserver) {
        this.onGoToPlaceObserver = onGoToPlaceObserver;
    }

    public OnBottomNavigationBar getOnBottomNavigationBarObserver() {
        return onBottomNavigationBarObserver;
    }

    public void setOnBottomNavigationBarObserver(OnBottomNavigationBar onBottomNavigationBarObserver) {
        this.onBottomNavigationBarObserver = onBottomNavigationBarObserver;
    }

    public interface OnGoToPlace{
        void goToPlace(LatLng latLng);
    }

    private OnGoToPlace onGoToPlaceObserver;
    private OnBottomNavigationBar onBottomNavigationBarObserver;

    private ArrayList<Place> places;

    public PlaceAdapter(){
        places = new ArrayList<>();
    }

    public void addPlace(Place place){

        places.add(place);
        this.notifyDataSetChanged();

    }

    public void clear(){
        places.clear();
    }

    public void update(){
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
        String rate = "Sin calificar aún";
        if(place.getRate()>0){
            rate = ""+place.getRate();
        }
        holder.getStarsTextView().setText(rate);

        if(places.get(position).getDistanceTo()!=-1)
            holder.getDistanceTextView().setText(""+place.getDistanceTo()+" m");
        else{
            holder.getDistanceTextView().setText(R.string.distance);
        }

        holder.getEyeListImageView().setOnClickListener((v)->{
            onGoToPlaceObserver.goToPlace(place.getMarker());
            onBottomNavigationBarObserver.goToMap(false);
        });

    }

    @Override
    public int getItemCount() {
        return places.size();
    }
}
