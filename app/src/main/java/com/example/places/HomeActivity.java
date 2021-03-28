package com.example.places;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {

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


}