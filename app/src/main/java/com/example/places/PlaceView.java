package com.example.places;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class PlaceView extends RecyclerView.ViewHolder {

    private ConstraintLayout root;
    private ImageView placePicImageView;
    private ImageView eyeListImageView;
    private TextView placeNameTextView;
    private TextView starsTextView;
    private TextView distanceTextView;

    public PlaceView(ConstraintLayout root) {
        super(root);

        this.root = root;
        placePicImageView = root.findViewById(R.id.placePicImageView);
        eyeListImageView = root.findViewById(R.id.eyeListImageView);
        placeNameTextView = root.findViewById(R.id.placeListNameTextView);
        starsTextView = root.findViewById(R.id.placeStarsListTextView);
        distanceTextView = root.findViewById(R.id.distanceTextView);




    }

    public ConstraintLayout getRoot() {
        return root;
    }

    public void setRoot(ConstraintLayout root) {
        this.root = root;
    }

    public ImageView getPlacePicImageView() {
        return placePicImageView;
    }

    public void setPlacePicImageView(ImageView placePicImageView) {
        this.placePicImageView = placePicImageView;
    }

    public ImageView getEyeListImageView() {
        return eyeListImageView;
    }

    public void setEyeListImageView(ImageView eyeListImageView) {
        this.eyeListImageView = eyeListImageView;
    }

    public TextView getPlaceNameTextView() {
        return placeNameTextView;
    }

    public void setPlaceNameTextView(TextView placeNameTextView) {
        this.placeNameTextView = placeNameTextView;
    }

    public TextView getStarsTextView() {
        return starsTextView;
    }

    public void setStarsTextView(TextView starsTextView) {
        this.starsTextView = starsTextView;
    }

    public TextView getDistanceTextView() {
        return distanceTextView;
    }

    public void setDistanceTextView(TextView distanceTextView) {
        this.distanceTextView = distanceTextView;
    }

}
