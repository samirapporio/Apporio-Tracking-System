package com.apporioinfolabs.ats_sdk.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.apporioinfolabs.ats_sdk.di.ApplicationContext;
import com.apporioinfolabs.ats_sdk.utils.ATSConstants;
import com.apporioinfolabs.ats_sdk.utils.LOGS;

import javax.inject.Inject;
import javax.inject.Singleton;

// consumer no 681331
// 9990923456

@Singleton
public class SharedPrefrencesManager {

    private static final String TAG = "SharedPrefrencesManager";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor ;



    @Inject
    public SharedPrefrencesManager( @ApplicationContext Context context){
        sharedPref = context.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }


    public void saveData(String key, String Value){
        editor.putString(key,Value);
        editor.commit();
    }

    public String fetchData(String key){
        return sharedPref.getString(key,"NA");
    }


    public Location getLastSavedLocationObject(){
        try{
            String[] locationStrinfArr = fetchData(ATSConstants.KEYS.LOCATION).split("_");
            Location location = new Location("GPS");
            location.setLatitude(Double.parseDouble(""+locationStrinfArr[0]));
            location.setLongitude(Double.parseDouble(""+locationStrinfArr[1]));
            location.setAccuracy(Float.parseFloat(""+locationStrinfArr[2]));
            location.setBearing(Float.parseFloat(""+locationStrinfArr[3]));
            return location ;
        }catch (Exception e){
            LOGS.e(TAG, ""+e.getMessage());
            return null;
        }

    }


}
