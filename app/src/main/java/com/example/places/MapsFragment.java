/* * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @author Nicolás Penagos Montoya
 * nicolas.penagosm98@gmail.com
 * * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.example.places;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.places.interfaces.OnBottomNavigationBar;
import com.example.places.model.Place;
import com.example.places.util.UtilImage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.location.LocationManager.*;

/*
 * This is the map and its features.
 */
public class MapsFragment extends Fragment implements LocationListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, View.OnClickListener, PlaceAdapter.OnGoToPlace{

    // -------------------------------------
    // Maps assets
    // -------------------------------------
    private LocationManager manager;
    private GoogleMap map;
    private Marker currentPlaceMarker;
    private String currentPlaceName;
    private ArrayList<Marker> markers;
    private LatLng openHere;

    // -------------------------------------
    // Global Variables
    // -------------------------------------
    private SharedPreferences preferences;
    private Gson gson;
    private Place[] places;
    private boolean onRating;
    private Place ratePlace;
    private boolean availableGPS;
    private boolean availableNetwork;
    private String provider;
    private int currentRate;
    private boolean showRate;

    // -------------------------------------
    // Address assets
    // -------------------------------------
    private Geocoder geocoder;
    private List<Address> addresses;

    // -------------------------------------
    // Observers
    // -------------------------------------
    private OnAddressSet addressObserver;
    private OnBottomNavigationBar onBottomNavigationBarObserver;
    private OnLocationChanged onLocationChangedObserver;


    // -------------------------------------
    // Views
    // -------------------------------------
    private ConstraintLayout topLayout;
    private ConstraintLayout bottomLayout;
    private Button cardButton;
    private TextView cardTextView;
    private TextView placeTextView;
    private TextView placeAddressTextView;
    private Button rateButton;
    private ImageView star1ImageView;
    private ImageView star2ImageView;
    private ImageView star3ImageView;
    private ImageView star4ImageView;
    private ImageView star5ImageView;
    private ImageView closePlaceImageView;

    public MapsFragment(){
        super();
        showRate = true;
    }


    // -------------------------------------
    // Global variables
    // -------------------------------------
    private boolean isVisible;
    private boolean allowMarkers;

    // -------------------------------------
    // UI Thread Methods
    // -------------------------------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_maps, container, false);

        topLayout = root.findViewById(R.id.topLayout);
        bottomLayout = root.findViewById(R.id.bottomLayout);
        cardButton = root.findViewById(R.id.cardButton);
        cardTextView = root.findViewById(R.id.cardText);
        placeTextView = root.findViewById(R.id.placeCardTitle);
        placeAddressTextView =  root.findViewById(R.id.placeCardAddress);
        rateButton = root.findViewById(R.id.rateButton);
        star1ImageView = root.findViewById(R.id.star1ImageView);
        star2ImageView = root.findViewById(R.id.star2ImageView);
        star3ImageView = root.findViewById(R.id.star3ImageView);
        star4ImageView = root.findViewById(R.id.star4ImageView);
        star5ImageView = root.findViewById(R.id.star5ImageView);
        closePlaceImageView = root.findViewById(R.id.closePlaceImageView);



        cardButton.setOnClickListener(this);
        star1ImageView.setOnClickListener(this);
        star2ImageView.setOnClickListener(this);
        star3ImageView.setOnClickListener(this);
        star4ImageView.setOnClickListener(this);
        star5ImageView.setOnClickListener(this);
        rateButton.setOnClickListener(this);

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        onRating = false;
        currentRate = -1;
        rateButton.setEnabled(false);
        rateButton.setAlpha(0.5f);


        if(!isVisible)
          topLayout.setVisibility(View.GONE);

        cardButton.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);

        preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);

        gson = new Gson();

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

    @Override
    public void onStop() {

        super.onStop();
        isVisible = false;
        allowMarkers = false;
        showRate = true;

        if(places!=null){
            for (int i=0;i<places.length;i++){
                places[i].setRated(false);
            }
        }

        String jsonPlaces = gson.toJson(places);
        preferences.edit().putString("places", jsonPlaces).apply();


    }

    // -------------------------------------
    // Map Methods
    // -------------------------------------
    @SuppressLint("MissingPermission")
    public void setInitialPos(){


        //Determina si el NETWORK_PROVIDER esta disponible:
        try {
            availableNetwork = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            provider = LocationManager.NETWORK_PROVIDER;
        } catch (Exception ex) {
            Log.e(">>>>","Error obtaining NETWORK_PROVIDER.");
        }


        //Determina si el GPS_PROVIDER esta disponible:
        try {
            availableGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            provider = LocationManager.GPS_PROVIDER;
        } catch (Exception ex) {
            Log.e(">>>>","Error obtaining GPS_PROVIDER.");
        }


        Location location = manager.getLastKnownLocation(provider);

        if(openHere!=null){
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(openHere, 16));
            openHere = null;
        }else{
            if(location!=null){

                LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 16));

            }
        }


    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

            onLocationChangedObserver.onLocationChanged(location);

            ratePlace = null;
            double minDistance = Double.POSITIVE_INFINITY;

            for (int i=0; i<places.length; i++){

                LatLng from = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng to = new LatLng(places[i].getMarker().latitude, places[i].getMarker().longitude);


                double distanceInMeters = SphericalUtil.computeDistanceBetween(from, to);


                if(distanceInMeters<100){
                    if(distanceInMeters<minDistance && ! places[i].isRated()){

                        ratePlace = places[i];
                        minDistance = distanceInMeters;
                        Place finalRatePlace = ratePlace;


                    }

                }


            }



            if(showRate){
                if(ratePlace!=null){

                    bottomLayout.setVisibility(View.VISIBLE);
                    placeTextView.setText(ratePlace.getName());
                    placeAddressTextView.setText(ratePlace.getAddress());
                    closePlaceImageView.setImageBitmap(UtilImage.createImageFromPath(ratePlace.getPath()));

                }
            }


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
        manager.requestLocationUpdates(GPS_PROVIDER, 1000, 2, (LocationListener) this);
        currentPlaceMarker = null;

        //Map listeners
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);

        setInitialPos();

        String placesJson = preferences.getString("places", "NO_PLACES");
        places= gson.fromJson(placesJson, Place[].class);

        for (int i=0; i<places.length; i++){
            
            Place currentPlace = places[i];
            Marker marker = map.addMarker(new MarkerOptions().position(currentPlace.getMarker()));
            marker.setTitle(currentPlace.getName());
            marker.setSnippet(currentPlace.getAddress());

        }

    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if(allowMarkers){

            ViewGroup.LayoutParams params = topLayout.getLayoutParams();
            topLayout.setLayoutParams(params);
            cardButton.setVisibility(View.VISIBLE);

            if(currentPlaceMarker==null){
                currentPlaceMarker = map.addMarker(new MarkerOptions().position(latLng));
            }else{
                currentPlaceMarker.setPosition(latLng);
            }

            if(currentPlaceMarker!=null){
                currentPlaceMarker.setTitle(currentPlaceName);
            }

            new Thread(
                    ()->{

                        try {

                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            String address = addresses.get(0).getAddressLine(0);

                            getActivity().runOnUiThread(()->{

                                currentPlaceMarker.hideInfoWindow();
                                currentPlaceMarker.setSnippet(address);
                                currentPlaceMarker.showInfoWindow();
                                addressObserver.onMarkerSet(currentPlaceMarker);

                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

            ).start();

        }

    }

    // -------------------------------------
    // Logic Methods
    // -------------------------------------
    public void showOptions(){
        isVisible = true;
    }

    public void addAddressToMarker(String address){
       getActivity().runOnUiThread(()->{ currentPlaceMarker.setSnippet(address);});
    }

    public void setCurrentPlaceName(String currentPlaceName) {
        allowMarkers = true;
        this.currentPlaceName = currentPlaceName;
    }

    public void setAddressObserver(OnAddressSet addressObserver) {
        this.addressObserver = addressObserver;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.cardButton:

                addressObserver.onAddressSet(addresses.get(0).getAddressLine(0));
                SharedPreferences preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
                preferences.edit().putString("address", addresses.get(0).getAddressLine(0)).apply();
                onBottomNavigationBarObserver.goToNew();

                break;

            case R.id.rateButton:

                onRating = false;
                ratePlace.setRated(true);

                String jsonPlaces = gson.toJson(places);
                ratePlace.setRated(true);
                ratePlace.addRate(currentRate);
                preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
                preferences.edit().putString("places", jsonPlaces).apply();
                bottomLayout.setVisibility(View.GONE);
                currentRate = -1;


                break;

            case R.id.star1ImageView:

                    currentRate = 1;
                    star1ImageView.setImageResource(R.drawable.pressed_star);
                    star2ImageView.setImageResource(R.drawable.star);
                    star3ImageView.setImageResource(R.drawable.star);
                    star4ImageView.setImageResource(R.drawable.star);
                    star5ImageView.setImageResource(R.drawable.star);
                    rateButton.setEnabled(true);
                    rateButton.setAlpha(1f);

                break;

            case R.id.star2ImageView:

                currentRate = 2;
                star1ImageView.setImageResource(R.drawable.pressed_star);
                star2ImageView.setImageResource(R.drawable.pressed_star);
                star3ImageView.setImageResource(R.drawable.star);
                star4ImageView.setImageResource(R.drawable.star);
                star5ImageView.setImageResource(R.drawable.star);
                rateButton.setEnabled(true);
                rateButton.setAlpha(1f);

                break;

            case R.id.star3ImageView:

                currentRate = 3;
                star1ImageView.setImageResource(R.drawable.pressed_star);
                star2ImageView.setImageResource(R.drawable.pressed_star);
                star3ImageView.setImageResource(R.drawable.pressed_star);
                star4ImageView.setImageResource(R.drawable.star);
                star5ImageView.setImageResource(R.drawable.star);
                rateButton.setEnabled(true);
                rateButton.setAlpha(1f);

                break;

            case R.id.star4ImageView:

                currentRate = 4;
                star1ImageView.setImageResource(R.drawable.pressed_star);
                star2ImageView.setImageResource(R.drawable.pressed_star);
                star3ImageView.setImageResource(R.drawable.pressed_star);
                star4ImageView.setImageResource(R.drawable.pressed_star);
                star5ImageView.setImageResource(R.drawable.star);
                rateButton.setEnabled(true);
                rateButton.setAlpha(1f);

                break;

            case R.id.star5ImageView:

                currentRate = 5;
                star1ImageView.setImageResource(R.drawable.pressed_star);
                star2ImageView.setImageResource(R.drawable.pressed_star);
                star3ImageView.setImageResource(R.drawable.pressed_star);
                star4ImageView.setImageResource(R.drawable.pressed_star);
                star5ImageView.setImageResource(R.drawable.pressed_star);
                rateButton.setEnabled(true);
                rateButton.setAlpha(1f);

                break;


        }

    }

    @Override
    public void goToPlace(LatLng latLng) {
        openHere = latLng;
    }


    // -------------------------------------
    // Interfaces
    // -------------------------------------
    public interface OnAddressSet{

        void onAddressSet(String address);
        void onMarkerSet(Marker marker);

    }



    public interface OnLocationChanged{
        void onLocationChanged(Location location);
    }


    // -------------------------------------
    // Getters and setters
    // -------------------------------------
    public OnBottomNavigationBar getOnBottomNavigationBarObserver() {
        return onBottomNavigationBarObserver;
    }

    public void setOnBottomNavigationBarObserver(OnBottomNavigationBar onBottomNavigationBar) {
        this.onBottomNavigationBarObserver = onBottomNavigationBar;
    }

    public void setOnLocationChangedObserver(OnLocationChanged onLocationChangedObserver){
        this.onLocationChangedObserver =  onLocationChangedObserver;
    }

    public boolean isShowRate(){
        return showRate;
    }

    public void setShowRate(boolean showRate){
        Log.e(">>>>", "Pongo falso");
        this.showRate = showRate;
    }



}