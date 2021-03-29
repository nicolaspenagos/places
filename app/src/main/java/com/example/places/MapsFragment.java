package com.example.places;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.location.LocationManager.*;

public class MapsFragment extends Fragment implements LocationListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, NewFragment.OnMapPlaceLocation {

    private LocationManager manager;
    private GoogleMap map;
    private Marker placeMarker;

    private boolean isVisible;

    // -------------------------------------
    // Views
    // -------------------------------------
    private ConstraintLayout bottomLayout;
    private Button cardButton;
    private TextView cardTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_maps, container, false);

        bottomLayout = root.findViewById(R.id.bottomLayout);
        cardButton = root.findViewById(R.id.cardButton);
        cardTextView = root.findViewById(R.id.cardText);



        if(!isVisible)
          bottomLayout.setVisibility(View.GONE);

        cardButton.setVisibility(View.GONE);

        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public void setInitialPos(){

        Location location = manager.getLastKnownLocation(GPS_PROVIDER);

        if(location!=null){

            LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 16));

        }

    }


    @Override
    public void onLocationChanged(@NonNull Location location) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMyLocationEnabled(true);
        setInitialPos();
        manager.requestLocationUpdates(GPS_PROVIDER, 1000, 2, (LocationListener) this);

        //Map listeners
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);


    }

    @Override
    public void onStop() {
        super.onStop();
        isVisible = false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        ViewGroup.LayoutParams params = bottomLayout.getLayoutParams();

        bottomLayout.setLayoutParams(params);
        cardButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPlaceNameUpdate(String placeName) {

    }

    @Override
    public void onGoToMap() {

    }

    public void showOptions(){

        isVisible = true;
    }
}