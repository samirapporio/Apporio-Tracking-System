package com.apporioinfolabs.ats_tracking_sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import com.apporioinfolabs.ats_sdk.ATS;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MeterTestActivity extends Activity {

    TextView gpsConnectedStatus, speedText, distanceText;
    Intent meterServiceintent ;
    Button startStopMeter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_test);
        gpsConnectedStatus = findViewById(R.id.gps_connected_status);
        speedText = findViewById(R.id.speed_text);
        distanceText = findViewById(R.id.distance_text);
        startStopMeter = findViewById(R.id.start_stop_meter);
        meterServiceintent = new Intent(this, MyService.class) ;

        if (ActivityCompat.checkSelfPermission(MeterTestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MeterTestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MeterTestActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }else{ runMeter(); }


        startStopMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ATS.isMeterRunning()){ ATS.stopMeter(); }
                else{ ATS.startMeter(); }
                toggleButton();
            }
        });

        toggleButton();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String s) {
        distanceText.setText(""+s);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    private void runMeter(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(this, "starting foreground service.", Toast.LENGTH_SHORT).show();
            startForegroundService(meterServiceintent);
        } else { // normal
            Toast.makeText(this, "Starting normal service.", Toast.LENGTH_SHORT).show();
            startService(meterServiceintent);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1 && grantResults[0] == 0){ runMeter(); }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void toggleButton(){
        if(ATS.isMeterRunning()){
            startStopMeter.setText("STOP Meter");
        }else{
            startStopMeter.setText("START Meter");
        }
    }



}
