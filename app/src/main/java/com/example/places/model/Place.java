/* * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @author Nicolás Penagos Montoya
 * nicolas.penagosm98@gmail.com
 * * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.example.places.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/*
 * This is the class that models a place;
 */
public class Place {

    // -------------------------------------
    // Attributes
    // -------------------------------------
    private String name;
    private String address;
    private String path;
    private LatLng marker;

    // -------------------------------------
    // Constructor
    // -------------------------------------
    public Place(){

    }

    public Place(String name, String address, String path, LatLng marker) {
        this.name = name;
        this.address = address;
        this.path = path;
        this.marker = marker;
    }

    // -------------------------------------
    // Getters and setters
    // -------------------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LatLng getMarker() {
        return marker;
    }

    public void setMarker(LatLng marker) {
        this.marker = marker;
    }

}
