package com.example.places;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class NewFragment extends Fragment implements View.OnClickListener{

    //State

    //

    // -------------------------------------
    // Views
    // -------------------------------------
    private EditText placeNameEditText;
    private ImageView addImageButton;
    private ImageView goToMapButton;
    private Button registerButton;


    public NewFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
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


        return root;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addImageButton:

                break;

            case R.id.goToMapButton:

                break;

            case R.id.registerButton:

                break;
        }
    }
}