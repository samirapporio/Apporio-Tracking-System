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

    public Location setLocation(double latitude, double longitude, float accuracy, float bearing, float speed){
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAccuracy(accuracy);
        location.setBearing(bearing);
        location.setSpeed(speed);
        return location ;
    }

    public Location getLocation(){
        return location ;
    }


}
