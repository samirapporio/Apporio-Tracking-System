package com.apporioinfolabs.ats_sdk.managers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import com.apporioinfolabs.ats_sdk.ATS;
import com.apporioinfolabs.ats_sdk.di.ApplicationContext;
import com.apporioinfolabs.ats_sdk.models.ModelLocation;
import com.apporioinfolabs.ats_sdk.utils.ATSConstants;
import com.apporioinfolabs.ats_sdk.utils.AppUtils;
import com.apporioinfolabs.ats_sdk.utils.LOGS;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;

import static androidx.core.content.PermissionChecker.checkSelfPermission;


@Singleton
public class AtsLocationManager {

    private FusedLocationProviderClient fusedLocationClient;
    private final int REQUEST_CHECK_SETTINGS = 34523;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Context mContext;
    private final static String TAG = "AtsLocationManager";

    @Inject SharedPrefrencesManager sharedPrefrencesManager ;
    @Inject NotificationManager notificationManager ;
    @Inject SocketManager socketManager ;
    @Inject DatabaseManager databaseManager ;
    @Inject ModelLocation modelLocation ;



    @Inject
    public AtsLocationManager(@ApplicationContext  Context context) {
        this.mContext = context;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    LOGS.d(TAG, "Getting null Location");
                    notificationManager.updateRunningNotificationView("Getting No Location from this device", true);
                    return;
                }else{
                    for (Location location : locationResult.getLocations()) {
                        try{
                            if(location.getAccuracy() <= ATS.mBuilder.minAcccuracy){
                                String locationString = ""+ location.getLatitude()+"_"+location.getLongitude()+"_"+location.getAccuracy()+"_"+location.getBearing()+"_"+location.getTime() ;
                                sharedPrefrencesManager.saveData(ATSConstants.KEYS.LOCATION, ""+ locationString);

                                EventBus.getDefault().post(modelLocation.setLocation(location.getLatitude(),location.getLongitude(),location.getAccuracy(),location.getBearing()));

                                notificationManager.updateRunningNotificationView(
                                        AppUtils.getLocationString(locationString)+
                                                " SQL Location stash:"+databaseManager.getAllLogsFromTable().size()+
                                                " Battery: "+ATS.mBatteryLevel+
                                                " State: "+(ATS.app_foreground?"Foreground":"Background")+
                                                " TAG: "+sharedPrefrencesManager.fetchData(ATSConstants.KEYS.TAG)
                                        , false );

                                try{
                                    if(socketManager.isSocketConnected()){
                                        socketManager.emitLocation(
                                                new JSONObject().put("ats_id",
                                                        ATS.getAtsid()).put("location",locationString+ "_"
                                                        +ATS.mBatteryLevel+"_"
                                                        +(ATS.app_foreground?"1":"0")+"_"
                                                        +sharedPrefrencesManager.fetchData(ATSConstants.KEYS.TAG)+"_"
                                                        +sharedPrefrencesManager.fetchData(ATSConstants.KEYS.DEVELOPER_ID)));
                                    }
                                    else{ databaseManager.addLocationLog(locationString+"_"+ATS.mBatteryLevel+"_"+(ATS.app_foreground?"1":"0")+"_"+sharedPrefrencesManager.fetchData(ATSConstants.KEYS.TAG)); }
                                }catch (Exception e){
                                    LOGS.e(TAG , ""+e.getMessage());
                                }
                            }
                        }catch (Exception e){
                        LOGS.e(TAG, ""+e.getMessage());
                        }
                    }
                }
            }
        };
        createLocationRequest();
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(ATS.mBuilder.LocationInterval);
        locationRequest.setFastestInterval(ATS.mBuilder.LocationInterval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(ATS.mBuilder.smallestDisplacement);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(mContext);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                LOGS.i(TAG, "Location Settings are satisfied");
                startLocationUpdates();
            }
        });


        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notificationManager.updateRunningNotificationView("Locations fetching criteria are not satisfied, please check your location settings", true);
                LOGS.e(TAG, "Location Settings are not satisfied , Please show some dialog over this event to take location properly");
            }
        });

    }

    @SuppressLint("WrongConstant")
    public void startLocationUpdates() {
        if(fusedLocationClient == null){
            LOGS.e(TAG , "Fused Location Client haven't yet created.");
        }else{
            if (checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                notificationManager.updateRunningNotificationView("It seems your locaiton permission is turned OFF. Please check it and turn it on in order to work this app properly", true);

                LOGS.e(TAG, "Found Location permission are missing ");
                return;
            }else{
                LOGS.i(TAG , "Location service started");
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
            }
        }


    }

    public void stopLocationUpdates(){
        try{
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }catch (Exception e){
            LOGS.e(TAG , ""+e.getMessage());
        }

    }

    private JSONObject getLocationObject(Location location, JSONObject locationJsonObject) throws Exception {
        locationJsonObject.put("latitude", location.getLatitude());
        locationJsonObject.put("longitude", location.getLongitude());
        locationJsonObject.put("accuracy", location.getAccuracy());
        locationJsonObject.put("bearing", location.getBearing());
        locationJsonObject.put("device_time", location.getTime());
        return locationJsonObject ;

    }




}
