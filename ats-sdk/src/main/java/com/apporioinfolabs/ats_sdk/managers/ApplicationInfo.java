package com.apporioinfolabs.ats_sdk.managers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import com.apporioinfolabs.ats_sdk.ATS;

import java.util.ArrayList;
import java.util.List;

public class ApplicationInfo {

    private static final String TAG = "ApplicationInfo";

    public static String getDeviceInfo(){
        return ""+ Settings.Secure.getString(ATS.mBuilder.mApplication.getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID) +"|"+ Build.BRAND+"|"+Build.MODEL + "|" + Build.VERSION.SDK_INT + "|" + "A";
    }

    public static String getApplicationInfo() throws PackageManager.NameNotFoundException {
        PackageManager pm = ATS.mBuilder.mApplication.getBaseContext().getPackageManager() ;
        String packagename = ATS.mBuilder.mApplication.getApplicationContext().getPackageName();
        return ""+(String) pm.getApplicationLabel(pm.getApplicationInfo(packagename, PackageManager.GET_META_DATA))
                +"|"+ATS.mBuilder.mApplication.getApplicationContext().getPackageName()+"|";
    }

    public static List<String> getApplicationServiceInfo(){
        List<String> services = new ArrayList<>();
        ActivityManager manager = (ActivityManager)ATS.mBuilder.mApplication.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> data = manager.getRunningServices(Integer.MAX_VALUE)  ;

        for(int i =0 ; i < data.size() ; i++){
            String[] g = data.get(i).service.getClassName().split("[.]") ;
            services.add(""+g[g.length -1]);
        }

        return  services;
    }

    public static String getLocationPermissions(){
        return ""+hasPermission("android.permission.ACCESS_FINE_LOCATION") ;

    }

    private static boolean hasPermission(String permission) {
        int res = ATS.mBuilder.mApplication.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static String getInfo() throws Exception{
        // deviceId | Brand | Model | SDK version | android |  App name | package name | services array | locaion permission
        String info = ""+getDeviceInfo() +"|"+ getApplicationInfo() + "|" + getApplicationServiceInfo() + "|" +getLocationPermissions(); ;
        return info ;
    }
}
