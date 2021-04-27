/* * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @author Nicolás Penagos Montoya
 * nicolas.penagosm98@gmail.com
 * * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.example.places;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.places.interfaces.OnBottomNavigationBar;
import com.example.places.model.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

/*
 * This is the list that contains all the pre-registered places.
 */
public class SearchFragment extends Fragment implements MapsFragment.OnLocationChanged{





    // -------------------------------------
    // Views
    // -------------------------------------
    private RecyclerView placesViewList;
    private LinearLayoutManager layoutManager;
    private PlaceAdapter adapter;


    // -------------------------------------
    // Global Variables
    // -------------------------------------
    private Place[] places;




    private OnBottomNavigationBar onBottomNavigationBarObserver;

    public SearchFragment() {
        // Required empty public constructor
        adapter = new PlaceAdapter();

    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        placesViewList = root.findViewById(R.id.placesViewList);

        layoutManager = new LinearLayoutManager(getContext());
        placesViewList.setLayoutManager(layoutManager);

        placesViewList.setAdapter(adapter);

        adapter.clear();

        SharedPreferences preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String placesJson = preferences.getString("places", "NO_PLACES");

        places= gson.fromJson(placesJson, Place[].class);

        for (int i = 0; i<places.length; i++){
            adapter.addPlace(places[i]);
        }

        return root;
    }

    public OnBottomNavigationBar getOnBottomNavigationBarObserver() {
        return onBottomNavigationBarObserver;
    }

    public void setOnBottomNavigationBarObserver(OnBottomNavigationBar onBottomNavigationBarObserver) {
        this.onBottomNavigationBarObserver = onBottomNavigationBarObserver;
        adapter.setOnBottomNavigationBarObserver(onBottomNavigationBarObserver);
    }


    @Override
    public void onLocationChanged(Location location) {



        if(places!=null){
            for (Place place : places) {

                LatLng from = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng to = new LatLng(place.getMarker().latitude, place.getMarker().longitude);

                double distanceInMeters = SphericalUtil.computeDistanceBetween(from, to);

                place.setDistanceTo(Math.round((float) distanceInMeters));


            }

            adapter.update();

        }

    }

    public PlaceAdapter getAdapter(){
        return adapter;
    }



}