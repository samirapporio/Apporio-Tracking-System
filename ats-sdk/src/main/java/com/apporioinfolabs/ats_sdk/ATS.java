package com.apporioinfolabs.ats_sdk;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apporioinfolabs.ats_sdk.managers.SocketManager;
import com.apporioinfolabs.ats_sdk.utils.ATSConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ATS {


    public static ATS.Builder mBuilder = null ;
    private static final String TAG = "ATS";
    public static Gson gson ;
    public static int  mBatteryLevel = 0;
    private static int activityReferences = 0;
    private static boolean isActivityChangingConfigurations = false;
    public static boolean app_foreground = false ;


    private static BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
             mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
    };



    public static ATS.Builder startInit(Application application){
        return new ATS.Builder(application) ;
    }

    private static void init(ATS.Builder inBuilder) {
        mBuilder = inBuilder ;
        gson = new GsonBuilder().create();
        inBuilder.mApplication.registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        inBuilder.mApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

                if (++activityReferences == 1 && !isActivityChangingConfigurations) {
                    app_foreground = true ;
                }
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                isActivityChangingConfigurations = activity.isChangingConfigurations();
                if (--activityReferences == 0 && !isActivityChangingConfigurations) {
                    app_foreground = false ;
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    public static class Builder {

        public  Application mApplication;
        public String AppId = "NA";
        public int LocationInterval = 6000 ;
        public boolean FetchLocationWhenVehicleIsStopped = false ;
        public boolean LogEnable = false ;
        // notification settings
        public boolean IsDevelperMode = false ;
        public PendingIntent PendingIntent = null ;
        public int NotificationIcon = 0;
        public String NotificationTittle = "Notification Title goes here";
        public String NotificationContent = "Notification Content text Goes here";
        public int SocketConnectedColor = Color.argb(4,0, 153, 76);  // material green color
        public int SocketDisConnectedColor = Color.argb(4,204, 0, 0);  // Bright Red color

        // Socket Settings
//        public String SocketEndPoint = "http://13.233.98.63:3005";
        public String SocketEndPoint = "http://atssdk.herokuapp.com";
        public int LocationLogStashSyncRate = 30 ; // this will sync 30 logs from the logs stash every time

//        TaxiSegmentActionHandler mTaxiSegmentActionHandler;
//        TaxiSegmentScreensLifeCycleHandler mTaxiSegmentScreensLifeCycleHandler;


        private Builder() {}

        private Builder(Application application) {
            mApplication = application;
        }

        public Builder setAppId(String appId){
            this.AppId = appId ;
            return this;
        }

        public Builder setLocationInterval(int interval){
            this.LocationInterval = interval ;
            return  this;
        }

        public Builder fetchLocationWhenVehicleIsStop(boolean value){
            this.FetchLocationWhenVehicleIsStopped = value ;
            return this;
        }

        public Builder enableLogs(boolean val){
            this.LogEnable = val ;
            return this ;
        }

        public Builder setDeveloperMode(boolean val){
            this.IsDevelperMode = val;
            return this ;
        }

        public Builder setNotificationPendingIntent(android.app.PendingIntent intent){
            this.PendingIntent = intent ;
            return this ;
        }

        public Builder setNotificationIcon (int icon){
            this.NotificationIcon = icon ;
            return this;
        }

        public Builder setNotificationTittle(String collapsedText){
            this.NotificationTittle = collapsedText ;
            return  this ;
        }

        public Builder setNotificationContent (String expandedText){
            this.NotificationContent = expandedText ;
            return this ;
        }

        public Builder setConnectedStateColor (int argb){
            this.SocketConnectedColor = argb ;
            return this;
        }

        public Builder setDisconnectedColor (int argb){
            this.SocketDisConnectedColor = argb ;
            return this;
        }

        public Builder setSocketEndPoint(String url){
            this.SocketEndPoint = url ;
            return this ;
        }

        public Builder BuildersetLogStashSyncRate(int SyncRate){
            this.LocationLogStashSyncRate = SyncRate ;
            return this ;
        }


//        public Builder setonElementClickHandler(TaxiSegmentActionHandler taxiSegmentActionHandler){
//            mTaxiSegmentActionHandler = taxiSegmentActionHandler;
//            return this;
//        }




        public void init() {
            ATS.init(this);
        }

    }




    public static void enableLogs(boolean value){
        if(mBuilder != null){
            mBuilder.LogEnable = value;
        }
    }


    public static String getAtsid(){
        return ""+ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).getString(ATSConstants.KEYS.ATS_ID, "NA");
    }


    public static void setTag(String tag, AtsOnTagSetListener atsOnTagSetListener){
        SocketManager.addTag(tag,atsOnTagSetListener);
    }

    public static String getTag(){
        return ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).getString(ATSConstants.KEYS.TAG,"NA");
    }


    public static void removeTag(AtsOnTagSetListener atsOnTagSetListener){
        SocketManager.removeTag(atsOnTagSetListener);
    }

    public static void startListeningAtsID(String targetAtsIdToListen, AtsOnAddMessageListener atsOnAddMessageListener){
        SocketManager.registerListenerForTargetAtsId(targetAtsIdToListen, atsOnAddMessageListener);
    }



    public static void stopListeningAtsId(String targetAtsIdToRemove , AtsOnRemoveMessageListener atsOnRemoveMessageListener){
        SocketManager.removeRegistrationFromServerAndStopListeningAtsId(targetAtsIdToRemove, atsOnRemoveMessageListener);
    }



    public static void removeAllListeners(AtsOnRemoveMessageListener atsOnRemoveMessageListener){
        SocketManager.removeAllExtraListeners(atsOnRemoveMessageListener);
    }


    public static void listenTagAccordingToRadius(String tag, double latitude, double longitude, int radiusInMeter, AtsTagListener atsTagListener){
        SocketManager.listenToTag(tag, latitude, longitude, radiusInMeter, atsTagListener);
    }



    public static void stopListeningTag(AtsOnTagSetListener atsOnTagSetListener){
        SocketManager.stopListeningTag( atsOnTagSetListener);
    }


    public static void startTrip(String tripIdentifier , AtsOnTripSetListener atsOnTripSetListener){
        SocketManager.startTrip(tripIdentifier, atsOnTripSetListener);
    }

    // before ending this trip it is very improtant to sync all pending location to socket server
    public static void endTrip(String tripIdentifier, AtsOnTripSetListener atsOnTripSetListener){
        SocketManager.endTrip(tripIdentifier, atsOnTripSetListener);
    }





}
