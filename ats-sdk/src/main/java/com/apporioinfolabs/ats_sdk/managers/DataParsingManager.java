package com.apporioinfolabs.ats_sdk.managers;

import com.apporioinfolabs.ats_sdk.ATS;
import com.apporioinfolabs.ats_sdk.models.ModelMessageType;
import com.apporioinfolabs.ats_sdk.utils.LOGS;

public class DataParsingManager {

    private static final String TAG = "DataParsingManager";


    public static String getMessageType ( String message){
        try{
            ModelMessageType modelMessageType = ATS.gson.fromJson(""+message,ModelMessageType.class);
            return  modelMessageType.getNameValuePairs().getType();
        }catch (Exception e){
            LOGS.e(TAG , ""+e.getMessage());
            return "NA";
        }
    }

}
