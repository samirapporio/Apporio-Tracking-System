package com.apporioinfolabs.ats_tracking_sdk;

import android.location.Location;

import com.apporioinfolabs.ats_sdk.AtsLocationServiceClass;

import org.greenrobot.eventbus.EventBus;

public class MyService extends AtsLocationServiceClass {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onReceiveLocation(Location location) {

    }


    @Override
    public void onDistanceUpdate(String distance, String speed) {
        EventBus.getDefault().post(new EventDistanceSpeed( distance, speed));
    }
}
