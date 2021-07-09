package com.apporioinfolabs.ats_tracking_sdk;

import android.app.Application;

import com.apporioinfolabs.ats_sdk.ATS;

public class MainApplication extends Application {

    ATS.Builder ats ;

    String authToken  = "APP ID GOES HERE" ;

    @Override
    public void onCreate() {
        super.onCreate();


        //
//        Intent intentOffline = new Intent(mContext,NotificationActionReceiver.class);
//        intentOffline.putExtra("action",NotificationActionReceiver.ACTION_OFFLINE);
//        PendingIntent pending_intent_offline = PendingIntent.getBroadcast(mContext,3,intentOffline,PendingIntent.FLAG_UPDATE_CURRENT);


        ATS.startInit(this)
                .setAppId(""+authToken)
                .fetchLocationWhenVehicleIsStop(false)
                .enableLogs(true)
                .setLocationInterval(6000)
                .setDeveloperMode(true)
                .setNotificationTittle("Main Application Name")
                .setNotificationContent("Some Content that will run once the location service is started.")
//                .setConnectedStateColor(Color.argb(0, 102, 0 , 204))
//                .setDisconnectedColor(Color.argb(0 , 255, 255, 102))
//                .setSocketEndPoint("http://192.168.1.33:3027")
                .setNotificationIcon(R.drawable.samll_notification_icon)
                .setMinimumAccuracyForMeter(20)
                .init();

    }
}
