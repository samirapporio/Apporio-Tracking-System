package com.apporioinfolabs.ats_sdk.utils;

import android.util.Log;

import com.apporioinfolabs.ats_sdk.ATS;


public class LOGS {

    private static String PRE_TAG = "ATS: ";


    public static void i(String tag, String message){
        printLog(tag,message,"i");
    }

    public static void d(String tag, String message){
        printLog(tag,message,"d");
    }

    public static void e(String tag, String message){
        printLog(tag,message,"e");
    }

    public static void w(String tag, String message){
        printLog(tag,message,"w");
    }


    private static void printLog(String TAG, String message, String type){
        if(ATS.mBuilder.LogEnable){
            switch (type){
                case "i":
                    Log.i(PRE_TAG+TAG, ""+message);
                    break;
                case "d":
                    Log.d(PRE_TAG+TAG, ""+message);
                    break;
                case "e":
                    Log.e(PRE_TAG+TAG, ""+message );
                    break;
                case "w":
                    Log.w(PRE_TAG+TAG, ""+message );
                    break;
            }

        }
    }


}
