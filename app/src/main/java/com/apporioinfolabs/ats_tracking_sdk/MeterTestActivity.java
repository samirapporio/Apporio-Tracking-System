package com.apporioinfolabs.ats_tracking_sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

    TextView  speedText, distanceText, travel_time, waiting_time, accuracy_text, location_text;
    Intent meterServiceintent ;
    Button startStopMeter;
    private Timer mTimer = null;
    private long recentLocationTime = 0;
    private Handler mHandler ;

    ShapeDrawable pgDrawable ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_test);
        speedText = findViewById(R.id.speed_text);
        distanceText = findViewById(R.id.distance_text);
        startStopMeter = findViewById(R.id.start_stop_meter);
        travel_time = findViewById(R.id.travel_time);
        waiting_time = findViewById(R.id.waiting_time);
        accuracy_text = findViewById(R.id.accuracy_text);
        location_text = findViewById(R.id.location_text);

        mHandler = new Handler();
        pgDrawable = new ShapeDrawable(new RoundRectShape(new float[] { 5, 5, 5, 5, 5, 5, 5, 5 },     null, null)); ;
        meterServiceintent = new Intent(this, MyService.class) ;

        if (mTimer != null) { mTimer.cancel(); }
        else { mTimer = new Timer(); }


        startStopMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ATS.isMeterRunning()){
                    ATS.stopMeter();
                    stopService(meterServiceintent);
                    mTimer.cancel();
                    mTimer = new Timer();
                }
                else{
                   startCatingValue();

                }
                toggleButton();
            }
        });

        toggleButton();
    }

    private void startCatingValue(){
        if (ActivityCompat.checkSelfPermission(MeterTestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MeterTestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MeterTestActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }else{
            distanceText.setText("0.0"); waiting_time.setText("0"); travel_time.setText("0");
            runService();
            ATS.startMeter();
            mTimer.scheduleAtFixedRate(new OverAllTimertask(), ATS.mBuilder.LocationInterval, ATS.mBuilder.LocationInterval);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventDistanceSpeed eventDistanceSpeed) {
        distanceText.setText(""+eventDistanceSpeed.distance);
        speedText.setText(""+eventDistanceSpeed.speed);
        recentLocationTime = System.currentTimeMillis()/1000;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceived(Location location) {
       setAccuracyProgress(location.getAccuracy());
       location_text.setText(""+location.getLatitude()+","+location.getLongitude());
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
            startForegroundService(meterServiceintent);
        } else {
            startService(meterServiceintent);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1 && grantResults[0] == 0){ startCatingValue(); toggleButton();}
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void toggleButton(){
        if(ATS.isMeterRunning()){
            startStopMeter.setText("STOP Meter");
            startStopMeter.setBackgroundColor(Color.parseColor("#FFF44336"));
        }else{
            startStopMeter.setText("START Meter");
            startStopMeter.setBackgroundColor(Color.parseColor("#4CAF50"));
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
                        speedText.setText("0");
                        accuracy_text.setText("--");
                        location_text.setText("--");
                    }
                }
            });
        }
    }

    private void setAccuracyProgress(float accuracy){
        if(accuracy <= 13){
            pgDrawable.getPaint().setColor(Color.parseColor("#FF4CAF50"));
        }if(accuracy > 13 && accuracy < 40){
            pgDrawable.getPaint().setColor(Color.parseColor("#FFFFEB3B"));
        }if(accuracy > 40){
            pgDrawable.getPaint().setColor(Color.parseColor("#FFF44336"));
        }


        ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
//        progress_accuracy.setProgressDrawable(progress);
        accuracy_text.setText(""+Math.round(accuracy));
//        progress_accuracy.setProgress(Math.round(accuracy));


    }


}
