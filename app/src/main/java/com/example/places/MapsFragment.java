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
import android.widget.TextView;

import com.example.places.interfaces.OnBottomNavigationBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.location.LocationManager.*;

/*
 * This is the map and its features.
 */
public class MapsFragment extends Fragment implements LocationListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    // -------------------------------------
    // Maps assets
    // -------------------------------------
    private LocationManager manager;
    private GoogleMap map;
    private Marker currentPlaceMarker;
    private String currentPlaceName;
    private ArrayList<Marker> markers;

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


    // -------------------------------------
    // Views
    // -------------------------------------
    private ConstraintLayout bottomLayout;
    private Button cardButton;
    private TextView cardTextView;

    // -------------------------------------
    // Global variables
    // -------------------------------------
    private boolean isVisible;

    // -------------------------------------
    // UI Thread Methods
    // -------------------------------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_maps, container, false);

        bottomLayout = root.findViewById(R.id.bottomLayout);
        cardButton = root.findViewById(R.id.cardButton);
        cardTextView = root.findViewById(R.id.cardText);

        cardButton.setOnClickListener(
                (v)->{

                    addressObserver.onAddressSet(addresses.get(0).getAddressLine(0));
                    SharedPreferences preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
                    preferences.edit().putString("address", addresses.get(0).getAddressLine(0)).apply();
                    onBottomNavigationBarObserver.goToNew();

                }
        );

        geocoder = new Geocoder(getContext(), Locale.getDefault());

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

    @Override
    public void onStop() {

        super.onStop();
        isVisible = false;

    }

    // -------------------------------------
    // Map Methods
    // -------------------------------------
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
        manager.requestLocationUpdates(GPS_PROVIDER, 1000, 2, (LocationListener) this);
        currentPlaceMarker = null;

        //Map listeners
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);

        setInitialPos();

    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        ViewGroup.LayoutParams params = bottomLayout.getLayoutParams();
        bottomLayout.setLayoutParams(params);
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
        this.currentPlaceName = currentPlaceName;
    }

    public void setAddressObserver(OnAddressSet addressObserver) {
        this.addressObserver = addressObserver;
    }



    // -------------------------------------
    // Interfaces
    // -------------------------------------
    public interface OnAddressSet{

        void onAddressSet(String address);
        void onMarkerSet(Marker marker);

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



}