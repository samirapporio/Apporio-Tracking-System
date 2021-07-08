package com.apporioinfolabs.ats_tracking_sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import com.apporioinfolabs.ats_sdk.ATS;
import com.apporioinfolabs.ats_sdk.AtsLocationServiceClass;
import com.apporioinfolabs.ats_sdk.utils.LOGS;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MeterTestActivity extends Activity {

    TextView  speedText, distanceText, travel_time, waiting_time;
    Intent meterServiceintent ;
    Button startStopMeter;
    private Timer mTimer = null;
    private long recentLocationTime = 0;
    private Handler mHandler ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_test);
        mHandler = new Handler();



        if (mTimer != null) { mTimer.cancel(); }
        else { mTimer = new Timer(); }


        speedText = findViewById(R.id.speed_text);
        distanceText = findViewById(R.id.distance_text);
        startStopMeter = findViewById(R.id.start_stop_meter);
        travel_time = findViewById(R.id.travel_time);
        waiting_time = findViewById(R.id.waiting_time);
        meterServiceintent = new Intent(this, MyService.class) ;




        startStopMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ATS.isMeterRunning()){
                    ATS.stopMeter();
                    stopService(meterServiceintent);
                    mTimer.cancel();
                }
                else{
                    if (ActivityCompat.checkSelfPermission(MeterTestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MeterTestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MeterTestActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        return;
                    }else{
                        runService();
                        ATS.startMeter();
                        mTimer.scheduleAtFixedRate(new OverAllTimertask(), ATS.mBuilder.LocationInterval, ATS.mBuilder.LocationInterval);
                    }

                }
                toggleButton();
            }
        });

        toggleButton();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventDistanceSpeed eventDistanceSpeed) {
        distanceText.setText(""+eventDistanceSpeed.distance);
        speedText.setText(""+eventDistanceSpeed.speed);
        recentLocationTime = System.currentTimeMillis()/1000;
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
    private void runService(){
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
        if(requestCode == 1 && grantResults[0] == 0){ runService(); ATS.startMeter(); }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void toggleButton(){
        if(ATS.isMeterRunning()){
            startStopMeter.setText("STOP Meter");
        }else{
            startStopMeter.setText("START Meter");
        }
    }


    class OverAllTimertask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    travel_time.setText(""+(Integer.parseInt(travel_time.getText().toString()) + (ATS.mBuilder.LocationInterval/1000)));
                    if((System.currentTimeMillis() /1000) - recentLocationTime > (ATS.mBuilder.LocationInterval/1000)){
                        waiting_time.setText(""+(Integer.parseInt(waiting_time.getText().toString()) + (ATS.mBuilder.LocationInterval/1000)));
                    }
                }
            });
        }
    }


}
