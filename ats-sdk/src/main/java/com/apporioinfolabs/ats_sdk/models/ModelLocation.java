package com.apporioinfolabs.ats_sdk.models;

import android.location.Location;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ModelLocation {

    Location location ;

    @Inject
    public ModelLocation(){
        location = new Location("");
    }

    public Location setLocation(double latitude, double longitude, float accuracy, float bearing){
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAccuracy(accuracy);
        location.setBearing(bearing);
        return location ;
    }



}
