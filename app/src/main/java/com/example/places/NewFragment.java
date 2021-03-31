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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.places.interfaces.OnBottomNavigationBar;

/*
 * This is where the user is able to register a place.
 */
public class NewFragment extends Fragment implements View.OnClickListener, MapsFragment.OnAddressSet {

    // -------------------------------------
    // Views
    // -------------------------------------
    private EditText placeNameEditText;
    private ImageView addImageButton;
    private ImageView goToMapButton;
    private Button registerButton;
    private TextView addressTextView;
    private TextView addressTextViewTitle;

    // -------------------------------------
    // Global variables
    // -------------------------------------
    private boolean recentChange;
    private String currentAddress;

    private OnMapPlaceLocation onMapPlaceLocationObserver;
    private OnBottomNavigationBar onBottomNavigationBarObserver;

    public void setOnMapPlaceLocationObserver(OnMapPlaceLocation observer) {
        this.onMapPlaceLocationObserver = observer;
    }

    public NewFragment() {

    }

    public static NewFragment newInstance() {

        NewFragment fragment = new NewFragment();
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
        View root = inflater.inflate(R.layout.fragment_new, container, false);

        placeNameEditText = root.findViewById(R.id.placeNameEditText);
        addImageButton = root.findViewById(R.id.addImageButton);
        goToMapButton = root.findViewById(R.id.goToMapButton);
        registerButton = root.findViewById(R.id.registerButton);
        addressTextView = root.findViewById(R.id.addressTextView);
        addressTextViewTitle = root.findViewById(R.id.addressTitleTextView);

        addImageButton.setOnClickListener(this);
        goToMapButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        goToMapButton.setAlpha(0.5f);
        goToMapButton.setEnabled(false);

        if(recentChange){

            addressTextViewTitle.setVisibility(View.VISIBLE);
            addressTextView.setText(currentAddress);
            recentChange = false;

        }else{

            addressTextViewTitle.setVisibility(View.INVISIBLE);
            addressTextView.setText("");

        }
       //

        placeNameEditText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = s.toString().trim();

                        if(!text.equals("")) {

                            goToMapButton.setAlpha(1f);
                            goToMapButton.setEnabled(true);

                        }else if(s.length()==0){

                            goToMapButton.setAlpha(0.5f);
                            goToMapButton.setEnabled(false);

                        }
                    }
                }
        );


        Log.e(">>>", "Holi");
        return root;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addImageButton:

                break;

            case R.id.goToMapButton:

                    onMapPlaceLocationObserver.onPlaceNameUpdate(placeNameEditText.getText().toString());
                    onBottomNavigationBarObserver.goToMap();

                break;

            case R.id.registerButton:

                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        String placeName = placeNameEditText.getText().toString();

        if(placeName!=null){

            SharedPreferences preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
            preferences.edit().putString("editText", placeName).apply();

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
        String placeName = preferences.getString("editText", "NO_PLACE");

        if(!placeName.equals("NO_PLACE")){
            placeNameEditText.setText(placeName);
        }
    }

    @Override
    public void onAddressSet(String address) {

        recentChange = true;
        currentAddress = address;

    }

    public OnBottomNavigationBar getOnBottomNavigationBarObserver() {
        return onBottomNavigationBarObserver;
    }

    public void setOnBottomNavigationBarObserver(OnBottomNavigationBar onBottomNavigationBarObserver) {
        this.onBottomNavigationBarObserver = onBottomNavigationBarObserver;
    }

    public interface OnMapPlaceLocation{
        void onPlaceNameUpdate(String placeName);
    }

}