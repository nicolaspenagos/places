package com.example.places;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NewFragment.OnMapPlaceLocation{

    private MapsFragment mapsFragment;
    private NewFragment newFragment;
    private SearchFragment searchFragment;
    private BottomNavigationView navigator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigator = findViewById(R.id.navigator);

        mapsFragment = new MapsFragment();
        newFragment = NewFragment.newInstance();
        searchFragment = SearchFragment.newInstance();

        newFragment.setObserver(this);
        mapsFragment.setAddressObserver(newFragment);

        //Permissions request
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 1
        );

        showFragment(newFragment);

        navigator.setOnNavigationItemSelectedListener(

                (menuItem) -> {

                    switch (menuItem.getItemId()){
                        case R.id.searchItem:
                            showFragment(searchFragment);
                        break;

                        case R.id.mapItem:
                            showFragment(mapsFragment);
                        break;

                        case R.id.newItem:
                            showFragment(newFragment);
                            break;

                    }
                    return true;
                }

        );

    }

    public void showFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }


    @Override
    public void onPlaceNameUpdate(String placeName) {
        mapsFragment.setCurrentPlaceName(placeName);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onGoToMap() {

        navigator.setSelectedItemId(R.id.mapItem);
        showFragment(mapsFragment);
        mapsFragment.showOptions();


    }
}