package com.apporioinfolabs.ats_sdk.managers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.apporioinfolabs.ats_sdk.ATS;
import com.apporioinfolabs.ats_sdk.R;
import com.apporioinfolabs.ats_sdk.di.ApplicationContext;
import com.apporioinfolabs.ats_sdk.utils.ATSConstants;
import com.apporioinfolabs.ats_sdk.utils.LOGS;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationManager  {

    @Inject SocketManager socketManager;
    @Inject SharedPrefrencesManager sharedPrefrencesManager ;
    @Inject DatabaseManager databaseManager ;

    private Context mContext ;
    private static final String TAG = "NotificationManager";
    public static final int NOTIF_ID = 1234;
    private static android.app.NotificationManager mNotificationManager;
    private static RemoteViews mBigRemoteViews;
    private static Notification mNotification;
    private static NotificationCompat.Builder mBuilder;
    private android.app.NotificationManager manager ;


    @Inject
    public NotificationManager(@ApplicationContext Context context){
        this.mContext = context ;
        mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBigRemoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification_big);
        mBuilder = new NotificationCompat.Builder(context, ATSConstants.CHANNEL_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { manager = mContext.getSystemService(android.app.NotificationManager.class); }
    }

    public void startNotificationView(Service service) throws Exception{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LOGS.d(TAG , "starting service for OREO or above");
            service.startForeground(1, getNotificationForOreoAndAbove().build());
        }else{
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
//                service.startForeground(NOTIF_ID, getDevelopmentNotificationForLessThanHoneyComb());
            }
            else{
                service.startForeground(NOTIF_ID, getNotificationForMoreThanHoneyComb().build());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationCompat.Builder getNotificationForOreoAndAbove(){
        NotificationChannel serviceChannel = new NotificationChannel(ATSConstants.CHANNEL_ID, "Foreground Service Channel", android.app.NotificationManager.IMPORTANCE_DEFAULT);
        serviceChannel.setSound(null, null);
        manager.createNotificationChannel(serviceChannel);
        return mBuilder
                .setSmallIcon(ATS.mBuilder.NotificationIcon )
                .setContentTitle(ATS.mBuilder.IsDevelperMode?"Development Mode"  :""+ATS.mBuilder.NotificationTittle )
                .setContentIntent(ATS.mBuilder.PendingIntent)
                .setColorized(true)
                .setColor(socketManager.isSocketConnected()? ATS.mBuilder.SocketConnectedColor : ATS.mBuilder.SocketDisConnectedColor )
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""+ATS.mBuilder.NotificationContent));
    }

    private NotificationCompat.Builder getNotificationForMoreThanHoneyComb(){


        mBuilder.setSmallIcon(ATS.mBuilder.NotificationIcon)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(ATS.mBuilder.PendingIntent)
                .setCustomBigContentView(mBigRemoteViews)
                .setTicker("Ticker Text");

        LOGS.d(TAG, ""+mBuilder);
        return mBuilder ;
    }

    public void updateRunningNotificationView(String data, boolean bothReleaseAndDeveloper){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            updateOreoAboveNotification(data, bothReleaseAndDeveloper);
        }else{
            NotifyNotification(""+data);
        }
    }

    public void updateNotificationSocketConnectivity (String connectivity ){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setColor(connectivity.equals(""+SocketManager.SOCKET_CONNECTED) ? ATS.mBuilder.SocketConnectedColor : ATS.mBuilder.SocketDisConnectedColor );
            manager.notify(1, mBuilder.build());
        }else{
            mBigRemoteViews.setTextViewText(R.id.socket_connected_state, connectivity.equals(""+SocketManager.SOCKET_CONNECTED)?"Socket: CONNECTED":"Socket: DISCONNECTED");
//            mBigRemoteViews.setTextColor(R.id.socket_connected_state, connectivity.equals(""+SocketManager.SOCKET_CONNECTED)?  ATS.mBuilder.SocketConnectedColor : ATS.mBuilder.SocketDisConnectedColor );
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                mNotificationManager.notify(NOTIF_ID, mNotification);
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mNotificationManager.notify(NOTIF_ID, mBuilder.build());
            }
        }
    }

    private void updateOreoAboveNotification(String data, boolean bothReleaseAndDeveloper){
        if(bothReleaseAndDeveloper){ // this will update notification view in both release and debug mode
            mBuilder.setColor(socketManager.isSocketConnected()? ATS.mBuilder.SocketConnectedColor : ATS.mBuilder.SocketDisConnectedColor );
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(data));
            manager.notify(1, mBuilder.build());
        }else{ // this is non mandatory therefore update only if developer mode enabled
            if(ATS.mBuilder.IsDevelperMode){
                mBuilder.setColor(socketManager.isSocketConnected()? ATS.mBuilder.SocketConnectedColor : ATS.mBuilder.SocketDisConnectedColor );
                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(data));
                manager.notify(1, mBuilder.build());
            }
        }
    }

    private void NotifyNotification(String data){

        if (ATS.mBuilder.IsDevelperMode) {
            mBigRemoteViews.setTextViewText(R.id.bigger_notification_text, ""+data);
            mBigRemoteViews.setTextViewText(R.id.socket_connected_state, SocketManager.isSocketConnected()?"Socket: CONNECTED":"Socket: DISCONNECTED");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                mNotificationManager.notify(NOTIF_ID, mNotification);
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mNotificationManager.notify(NOTIF_ID, mBuilder.build());
            }
        }

    }





}
