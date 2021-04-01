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

import com.example.places.interfaces.OnBottomNavigationBar;
import com.example.places.util.UtilDomi;
import com.google.gson.Gson;

import java.io.File;

/*
 * This is where the user is able to register a place.
 */
public class NewFragment extends Fragment implements View.OnClickListener, MapsFragment.OnAddressSet {

    // -------------------------------------
    // Constants
    // -------------------------------------
    public final static int LOADED_FROM_CAMERA = 1;
    public final static int LOADED_FROM_GALLERY = 2;
    public final static int NO_LOADED = 0;

    // -------------------------------------
    // Views
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
    private boolean placeNameOk;
    private boolean addressOk;
    private boolean imageOk;
    private boolean imageFromGallery;
    private Gson gson;
    private Bitmap currentImage;
    private Uri uri;
    private int imageLoaded;


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

        gson =  new Gson();



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
              createImage();
        }else if(requestCode == GALLERY_CALLBACK && resultCode == getActivity().RESULT_OK){
            loadImage(data.getData());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addImageButton:

                imageFromGallery = true;
                Intent j = new Intent(Intent.ACTION_GET_CONTENT);
                j.setType("image/*");
                startActivityForResult(j, GALLERY_CALLBACK);


                break;

            case R.id.goToMapButton:

                    onMapPlaceLocationObserver.onPlaceNameUpdate(placeNameEditText.getText().toString());
                    onBottomNavigationBarObserver.goToMap();

                break;

            case R.id.registerButton:

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

        SharedPreferences preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
        String placeName = placeNameEditText.getText().toString();

        if(placeName!=null)
            preferences.edit().putString("editText", placeName).apply();


        if(currentAddress!=null && !currentAddress.equals(""))
            preferences.edit().putString("address", currentAddress).apply();


        preferences.edit().putBoolean("loadImage", imageOk).apply();

        if(file!=null){

            String jsonFile = gson.toJson(file);
            preferences.edit().putString("file", jsonFile).apply();

        }




    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
        String bitmapJson = preferences.getString("bitmap", "NO_BITMAP");
        String placeName = preferences.getString("editText", "NO_PLACE");
        String placeAddress = preferences.getString("address", "NO_ADDRESS");

        int loadedIndex =  preferences.getInt("loadedIndex", NO_LOADED);

        if(loadedIndex == LOADED_FROM_CAMERA){

            String fileJson = preferences.getString("file", "NO_FILE");

            if(!fileJson.equals("NO_FILE")){

                file = gson.fromJson(fileJson, File.class);
                createImage();

            }

        }else if(loadedIndex == LOADED_FROM_GALLERY){

            String uriJson = preferences.getString("uri", "NO_URI");

            if(!uriJson.equals("NO_URI")){
            //  loadImage(gson.fromJson(uriJson, Uri.class));
            }

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

            if(!currentAddress.equals("")){

                addressTextViewTitle.setVisibility(View.INVISIBLE);
                addressOk = false;

            }

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

    public void createImage(){

        Bitmap image = BitmapFactory.decodeFile(file.getPath());
        currentImage = rotateBitmap(scaleBitmap(image));
        placeImageView.setImageBitmap(currentImage);
        imageOk = true;

        SharedPreferences preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
        preferences.edit().putInt("loadedIndex", LOADED_FROM_CAMERA).apply();
        preferences.edit().putString("file", gson.toJson(file)).apply();

    }


    public void loadImage(Uri uri){

        String path = UtilDomi.getPath(getContext(), uri);
        Bitmap image = BitmapFactory.decodeFile(path);
        currentImage = rotateBitmap(scaleBitmap(image));
        placeImageView.setImageBitmap(currentImage);
        imageOk = true;

        SharedPreferences preferences = getContext().getSharedPreferences("NewFragment", Context.MODE_PRIVATE);
        preferences.edit().putInt("loadedIndex", LOADED_FROM_GALLERY).apply();
        preferences.edit().putString("uri", gson.toJson(uri)).apply();

    }

    public Bitmap scaleBitmap(Bitmap image){
        return  Bitmap.createScaledBitmap(
                image, image.getWidth()/12, image.getHeight()/12, true
        );
    }

    public Bitmap rotateBitmap(Bitmap thumbnail){

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);

    }


}