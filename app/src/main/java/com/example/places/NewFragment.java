/* * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @author Nicolás Penagos Montoya
 * nicolas.penagosm98@gmail.com
 * * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.example.places;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
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
import android.widget.Toast;

import com.example.places.interfaces.OnBottomNavigationBar;
import com.example.places.model.Place;
import com.example.places.util.UtilDomi;
import com.example.places.util.UtilImage;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

/*
 * This is where the user is able to register a place.
 */
public class NewFragment extends Fragment implements View.OnClickListener, MapsFragment.OnAddressSet {

    // -------------------------------------
    // Constants
    // -------------------------------------
    public final static int CAMERA_CALLBACK = 10;
    public final static int GALLERY_CALLBACK = 11;

    // -------------------------------------
    // Views
    // -------------------------------------
    private EditText placeNameEditText;
    private ImageView openGalleryButton;
    private ImageView goToMapButton;
    private ImageView openCameraButton;
    private ImageView placeImageView;
    private Button registerButton;
    private TextView addressTextView;
    private TextView addressTextViewTitle;

    // -------------------------------------
    // Global variables
    // -------------------------------------
    private boolean recentChange;
    private String currentAddress;
    private File file;
    private Gson gson;
    private boolean placeNameOk;
    private boolean addressOk;
    private boolean imageOk;
    private SharedPreferences preferences;
    private LatLng currentMarkerLatLng;
    private ArrayList<Place> places;
    private String path;

    // -------------------------------------
    // Observer pattern
    // -------------------------------------
    private OnMapPlaceLocation onMapPlaceLocationObserver;
    private OnBottomNavigationBar onBottomNavigationBarObserver;

    // -------------------------------------
    // Constructor
    // -------------------------------------
    public NewFragment() {

    }




    public static NewFragment newInstance() {

        NewFragment fragment = new NewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }

    // -------------------------------------
    // UI Thread Methods
    // -------------------------------------
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
        openGalleryButton = root.findViewById(R.id.addImageButton);
        goToMapButton = root.findViewById(R.id.goToMapButton);
        registerButton = root.findViewById(R.id.registerButton);
        addressTextView = root.findViewById(R.id.addressTextView);
        addressTextViewTitle = root.findViewById(R.id.addressTitleTextView);
        openCameraButton = root.findViewById(R.id.openCameraButton);
        placeImageView = root.findViewById(R.id.placeImageView);

        openGalleryButton.setOnClickListener(this);
        goToMapButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        openCameraButton.setOnClickListener(this);

        goToMapButton.setAlpha(0.5f);
        goToMapButton.setEnabled(false);

        placeNameOk = false;
        addressOk =  false;
        imageOk = false;

        gson =  new Gson();
        preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);



        String placesJson = preferences.getString("places", "NO_PLACES");
        if(!placesJson.equals("NO_PLACES")){
            places = gson.fromJson(placesJson, ArrayList.class);
        }else{
            places = new ArrayList<Place>();
        }

        if(recentChange){

            addressTextViewTitle.setVisibility(View.VISIBLE);
            addressTextView.setText(currentAddress);
            recentChange = false;

        }else{

            addressTextViewTitle.setVisibility(View.INVISIBLE);
            addressTextView.setText("");

        }

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
                            placeNameOk = true;

                        }else if(s.length()==0){

                            goToMapButton.setAlpha(0.5f);
                            goToMapButton.setEnabled(false);
                            placeNameOk = false;

                        }
                    }
                }
        );

        return root;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_CALLBACK && resultCode == getActivity().RESULT_OK){
              createImage(file.getPath());
        }else if(requestCode == GALLERY_CALLBACK && resultCode == getActivity().RESULT_OK){
            createImage(UtilDomi.getPath(getContext(), data.getData()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.addImageButton:

                Intent j = new Intent(Intent.ACTION_GET_CONTENT);
                j.setType("image/*");
                startActivityForResult(j, GALLERY_CALLBACK);

                break;

            case R.id.goToMapButton:

                    onMapPlaceLocationObserver.onPlaceNameUpdate(placeNameEditText.getText().toString());
                    onBottomNavigationBarObserver.goToMap(true);

                break;

            case R.id.registerButton:

                if(imageOk && addressOk && placeNameOk){

                    Place place = new Place(placeNameEditText.getText().toString(), currentAddress, path, currentMarkerLatLng);
                    places.add(place);
                    String jsonPlaces = gson.toJson(places);
                    preferences.edit().putString("places", jsonPlaces).apply();
                    emptyData();

                }else{
                    Toast.makeText(getContext(), R.string.toast_fill_all, Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.openCameraButton:

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = new File( getActivity().getExternalFilesDir(null) + "/photo.png");
                Uri uri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName(), file);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, CAMERA_CALLBACK);

                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        String placeName = placeNameEditText.getText().toString();

        if(placeName!=null)
            preferences.edit().putString("editText", placeName).apply();


        if(currentAddress!=null && !currentAddress.equals(""))
            preferences.edit().putString("address", currentAddress).apply();

        if(file!=null){

            String jsonFile = gson.toJson(file);
            preferences.edit().putString("file", jsonFile).apply();

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        String placeName = preferences.getString("editText", "NO_PLACE");
        String placeAddress = preferences.getString("address", "NO_ADDRESS");
        String path =  preferences.getString("path", "NO_PATH");

        if(!path.equals("NO_PATH")){
            createImage(path);
        }

        if(!placeName.equals("NO_PLACE")){

            placeNameEditText.setText(placeName);
            placeNameOk = true;

        }
        if(!placeAddress.equals("NO_ADDRESS")){

            currentAddress = placeAddress;
            addressTextView.setText(currentAddress);
            addressTextViewTitle.setVisibility(View.VISIBLE);
            addressOk = true;


        }

    }

    // -------------------------------------
    // Logic Methods
    // -------------------------------------
    @Override
    public void onAddressSet(String address) {

        recentChange = true;
        currentAddress = address;

    }

    @Override
    public void onMarkerSet(Marker marker) {
        currentMarkerLatLng = marker.getPosition();
    }

    public interface OnMapPlaceLocation{
        void onPlaceNameUpdate(String placeName);
    }

    public void createImage(String path){

        Bitmap image = BitmapFactory.decodeFile(path);
        placeImageView.setImageBitmap(UtilImage.rotateBitmap(UtilImage.scaleBitmap(image)));
        SharedPreferences preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
        preferences.edit().putString("path", path).apply();
        this.path = path;
        imageOk = true;

    }

    public void emptyData(){

        imageOk = false;
        addressOk = false;
        placeNameOk = false;

        currentMarkerLatLng = null;
        currentAddress = "";
        placeNameEditText.setText("");
        placeImageView.setImageDrawable(null);

        addressTextView.setText("");
        addressTextViewTitle.setVisibility(View.INVISIBLE);

        preferences.edit().putString("address", "NO_ADDRESS").apply();
        preferences.edit().putString("path", "NO_PATH").apply();
        preferences.edit().putString("editText", "NO_PLACE").apply();


    }

    // -------------------------------------
    // Getters and setters
    // -------------------------------------
    public void setOnMapPlaceLocationObserver(OnMapPlaceLocation observer) {
        this.onMapPlaceLocationObserver = observer;
    }

    public OnBottomNavigationBar getOnBottomNavigationBarObserver() {
        return onBottomNavigationBarObserver;
    }

    public void setOnBottomNavigationBarObserver(OnBottomNavigationBar onBottomNavigationBarObserver) {
        this.onBottomNavigationBarObserver = onBottomNavigationBarObserver;
    }
    
}