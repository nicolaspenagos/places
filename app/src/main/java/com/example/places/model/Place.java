/* * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @author Nicolás Penagos Montoya
 * nicolas.penagosm98@gmail.com
 * * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.example.places.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

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
    private boolean rated;
    private int distanceTo;
    private int rateSum;
    private int rateCounter;

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
        this.rated = false;
        this.distanceTo = -1;
        this.rateSum = 0;
        this.rateCounter = 0;
    }

    public void addRate(int rate){

        rateSum += rate;
        rateCounter++;
    }

    public double getRate(){

        if(rateCounter==0){
            return -1.0;
        }

        double average = ((double)rateSum)/((double)rateCounter);
        return Math.round(average*100.0)/100.0;

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

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }

    public int getDistanceTo() {
        return distanceTo;
    }

    public void setDistanceTo(int distanceTo) {
        this.distanceTo = distanceTo;
    }
}
