/* * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @author Nicolás Penagos Montoya
 * nicolas.penagosm98@gmail.com
 * * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.example.places;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.places.interfaces.OnBottomNavigationBar;
import com.example.places.model.Place;
import com.google.gson.Gson;

/*
 * This is the list that contains all the pre-registered places.
 */
public class SearchFragment extends Fragment {

    // -------------------------------------
    // Views
    // -------------------------------------
    private EditText placeNameEditText;
    private RecyclerView placesViewList;
    private LinearLayoutManager layoutManager;
    private PlaceAdapter adapter;



    private OnBottomNavigationBar onBottomNavigationBarObserver;

    public SearchFragment() {
        // Required empty public constructor
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

        placeNameEditText = root.findViewById(R.id.placeEditText);
        placesViewList = root.findViewById(R.id.placesViewList);

        layoutManager = new LinearLayoutManager(getContext());
        placesViewList.setLayoutManager(layoutManager);
        adapter = new PlaceAdapter();
        placesViewList.setAdapter(adapter);

        SharedPreferences preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String placesJson = preferences.getString("places", "NO_PLACES");
        Place[] places= gson.fromJson(placesJson, Place[].class);

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
    }
}