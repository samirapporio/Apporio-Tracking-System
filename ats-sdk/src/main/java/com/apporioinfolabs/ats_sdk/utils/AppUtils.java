package com.apporioinfolabs.ats_sdk.utils;

import com.apporioinfolabs.ats_sdk.ATS;
import com.apporioinfolabs.ats_sdk.models.LocationLogs;

import org.json.JSONObject;

import java.util.List;

public class AppUtils {

    private static final String TAG = "AppUtils";


    public static String getLocationString(String locationString ){
        String location = "";
        String locatioSplitter[] = locationString.split("_");
        location = "Loc: "+locatioSplitter[0]+", "+locatioSplitter[1]+"  Acc: "+locatioSplitter[2]+" Bear: "+locatioSplitter[3];
        return location ;
    }

    public static String getLocationStashString (List<LocationLogs> locationLogs ){
        try{
            String locationStash = "";
            for(int i =0 ; i < locationLogs.size() ; i++){
                locationStash = locationStash+""+locationLogs.get(i).get_log()+"@";
            }
            return ""+new JSONObject().put("ats_id", ATS.getAtsid()).put("location",locationStash) ;
        }catch (Exception e){
            LOGS.e(TAG , ""+e.getMessage());
            return "NA" ;
        }


    }
}
