/* * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @author Nicolás Penagos Montoya
 * nicolas.penagosm98@gmail.com
 * * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.example.places;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.places.interfaces.OnBottomNavigationBar;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;

/*
 * This is the main activity and hosts all the Fragments.
 */
public class HomeActivity extends AppCompatActivity implements NewFragment.OnMapPlaceLocation, OnBottomNavigationBar {

    // -------------------------------------
    // Constants
    // -------------------------------------
    public final static int PERMISSIONS_CALLBACK = 11;

    // -------------------------------------
    // Fragments
    // -------------------------------------
    private MapsFragment mapsFragment;
    private NewFragment newFragment;
    private SearchFragment searchFragment;

    // -------------------------------------
    // Views
    // -------------------------------------
    private BottomNavigationView navigator;

    // -------------------------------------
    // UI Thread Methods
    // -------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigator = findViewById(R.id.navigator);

        mapsFragment = new MapsFragment();
        newFragment = NewFragment.newInstance();
        searchFragment = SearchFragment.newInstance();

        newFragment.setOnMapPlaceLocationObserver(this);
        newFragment.setOnBottomNavigationBarObserver(this);
        mapsFragment.setOnBottomNavigationBarObserver(this);
        searchFragment.setOnBottomNavigationBarObserver(this);
        mapsFragment.setAddressObserver(newFragment);


        //Permissions request
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                PERMISSIONS_CALLBACK
        );

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

        showFragment(newFragment);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSIONS_CALLBACK){

            boolean allGrant = true;
            for (int i = 0; i<grantResults.length && allGrant; i++){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allGrant = false;
                }
            }

            if(allGrant)
                Toast.makeText(this, R.string.allgrant, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, R.string.no_allgrant, Toast.LENGTH_LONG).show();

        }

    }


    // -------------------------------------
    // Logic methods
    // -------------------------------------
    public void showFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    // -------------------------------------
    // Interfaces methods
    // -------------------------------------
    @Override
    public void onPlaceNameUpdate(String placeName) {
        mapsFragment.setCurrentPlaceName(placeName);
    }


    @Override
    public void goToMap() {

        navigator.setSelectedItemId(R.id.mapItem);
        showFragment(mapsFragment);
        mapsFragment.showOptions();

    }

    @Override
    public void goToNew() {

        navigator.setSelectedItemId(R.id.newItem);
        showFragment(newFragment);

    }

    @Override
    public void goToSearch() {

        navigator.setSelectedItemId(R.id.searchItem);
        showFragment(searchFragment);

    }
}