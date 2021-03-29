package com.example.places;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class NewFragment extends Fragment implements View.OnClickListener {

    // -------------------------------------
    // Views
    // -------------------------------------
    private EditText placeNameEditText;
    private ImageView addImageButton;
    private ImageView goToMapButton;
    private Button registerButton;

    private OnMapPlaceLocation observer;

    public void setObserver(OnMapPlaceLocation observer) {
        this.observer = observer;
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

        addImageButton.setOnClickListener(this);
        goToMapButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        goToMapButton.setAlpha(0.5f);
        goToMapButton.setEnabled(false);

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


        return root;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addImageButton:

                break;

            case R.id.goToMapButton:

                    observer.onGoToMap();

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

    public interface OnMapPlaceLocation{
        void onPlaceNameUpdate(String placeName);
        void onGoToMap();
    }

}