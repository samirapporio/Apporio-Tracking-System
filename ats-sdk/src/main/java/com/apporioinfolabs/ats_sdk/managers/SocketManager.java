package com.apporioinfolabs.ats_sdk.managers;

import android.content.Context;
import android.os.Handler;

import com.apporioinfolabs.ats_sdk.ATS;
import com.apporioinfolabs.ats_sdk.AtsOnAddMessageListener;
import com.apporioinfolabs.ats_sdk.AtsOnRemoveMessageListener;
import com.apporioinfolabs.ats_sdk.AtsOnTagSetListener;
import com.apporioinfolabs.ats_sdk.AtsOnTripSetListener;
import com.apporioinfolabs.ats_sdk.AtsTagListener;
import com.apporioinfolabs.ats_sdk.models.LocationLogs;
import com.apporioinfolabs.ats_sdk.models.ModelResultChecker;
import com.apporioinfolabs.ats_sdk.models.ModelTag;
import com.apporioinfolabs.ats_sdk.models.ModelTagListener;
import com.apporioinfolabs.ats_sdk.models.ModelTripEnd;
import com.apporioinfolabs.ats_sdk.utils.ATSConstants;
import com.apporioinfolabs.ats_sdk.utils.AppUtils;
import com.apporioinfolabs.ats_sdk.utils.LOGS;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SocketManager {

    private static final String TAG = "SocketManager";
    private static Socket mSocket;

    // LISTENING KEYS
    public static String CONNECT = "connect";
    public static String DISCONNECT = "disconnect";
    public static String SOCKET_CONNECTED = "SOCKET CONNECTED";
    public static String SOCKET_DISCONNECTED = "SOCKET DISCONNECTED";
    public static String LOG_STASH_UPDATED = "LOG_STASH_UPDATED";
    public static String TAG_UPDATED = "TAG_UPDATED";


    // EMITTER KEYS
    public static String CONNECT_DEVICE = "CONNECT_DEVICE";
    public static String LOCATION = "LOCATION";
    public static String LOCATION_STASH = "LOCATION_STASH";
    public static String REGISTER_ATS_ID_LISTENER = "REGISTER_ATS_ID_LISTENER";
    public static String REMOVE_ATS_ID_REGISTRATION = "REMOVE_ATS_ID_REGISTRATION";
    public static String ADD_TAG = "ADD_TAG";
    public static String REMOVE_TAG = "REMOVE_TAG";
    public static String LISTEN_TAG = "LISTEN_TAG";
    public static String STOP_LISTEN_TAG = "STOP_LISTEN_TAG";
    public static String START_TRIP = "START_TRIP";
    public static String STOP_TRIP = "STOP_TRIP";

    @Inject DatabaseManager databaseManager ;
    private static Handler mHandler;



    @Inject
    public SocketManager(){
        try{
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.query = ""+ATS.mBuilder.AppId ;


            mSocket = IO.socket(""+ ATS.mBuilder.   SocketEndPoint, opts);
            mSocket.on(CONNECT, onConnect);
            mSocket.on(DISCONNECT, onDisconnected);
            mSocket.connect();

            mHandler = new Handler();



        } catch (Exception e){
            LOGS.e(TAG , ""+e.getMessage());
        }
    }



    // LISTENERS
    public  Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try{
                emitDevice(ApplicationInfo.getInfo());
                startLocationStashEmission();
            }
            catch (Exception e){ LOGS.e(TAG , ""+e.getMessage()); }
            EventBus.getDefault().post(SOCKET_CONNECTED);

        }
    };

    public  Emitter.Listener onDisconnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            EventBus.getDefault().post(SOCKET_DISCONNECTED);
            LOGS.e(TAG , "Device Disconnected From Socket Server. ");

        }
    };

    public  static Emitter.Listener onAtsIdMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LOGS.w(TAG , ""+ ATS.gson.toJson(args[0]));
            LOGS.w(TAG , ""+DataParsingManager.getMessageType(""+ ATS.gson.toJson(args[0])));
        }
    };

    public static void registerListenerForTargetAtsId(final String targetAtsIdToListen, final AtsOnAddMessageListener atsOnAddMessageListener){
        try{
            if(mSocket.connected()){
                mSocket.emit(REGISTER_ATS_ID_LISTENER,new JSONObject().put("sender_ats_id",ATS.getAtsid()).put("listening_ats_id",targetAtsIdToListen), new Ack() {
                    @Override
                    public void call(Object... args) {
                         ModelResultChecker modelResultChecker = ATS.gson.fromJson(""+args[0],ModelResultChecker.class);
                        if(modelResultChecker.getResult() == 1){

                            handerForAddMessageListener(""+modelResultChecker.getMessage(), atsOnAddMessageListener, 1);
                            mSocket.off(targetAtsIdToListen);
                            try{removeLlisteningKeyFromPrefrences(targetAtsIdToListen);} catch (Exception e){LOGS.e(TAG, ""+e.getMessage());}
                            try{addListeningKeyInPrefrences(targetAtsIdToListen);} catch (Exception e){LOGS.e(TAG, ""+e.getMessage());}
                            mSocket.on(targetAtsIdToListen, new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    handerForAddMessageListener("Incoming data after registration some key :  "+args[0], atsOnAddMessageListener,3);
                                }
                            });
                        }else{
                            handerForAddMessageListener(""+modelResultChecker.getMessage(), atsOnAddMessageListener, 2);
                        }
                    }
                });
            }else{
                handerForAddMessageListener("Socket is not connected to server.", atsOnAddMessageListener, 2);
            }
        }catch (Exception e){
            handerForAddMessageListener(""+e.getMessage(), atsOnAddMessageListener,2);
        }
    }

    public static void removeRegistrationFromServerAndStopListeningAtsId(final String targetAtsIdRemove, final AtsOnRemoveMessageListener atsOnRemoveMessageListener){
        try{
            if(mSocket.connected()){
                mSocket.emit(REMOVE_ATS_ID_REGISTRATION,new JSONObject().put("sender_ats_id",ATS.getAtsid()).put("listening_ats_id",targetAtsIdRemove), new Ack() {
                    @Override
                    public void call(Object... args) {
                        ModelResultChecker modelResultChecker = ATS.gson.fromJson(""+args[0],ModelResultChecker.class);
                        if(modelResultChecker.getResult() == 1){
                            handlerForRemoveMessageListeners(""+modelResultChecker.getMessage(), atsOnRemoveMessageListener, 1);
                            mSocket.off(targetAtsIdRemove);
                            try{removeLlisteningKeyFromPrefrences(targetAtsIdRemove);}catch (Exception e){LOGS.e(TAG , ""+e.getMessage());}
                        }else{
                            handlerForRemoveMessageListeners(""+modelResultChecker.getMessage(), atsOnRemoveMessageListener, 2);
                        }
                    }
                });
            }else{
                handlerForRemoveMessageListeners("Socket is not connected to server.", atsOnRemoveMessageListener, 2);
            }
        }catch (Exception e){
            handlerForRemoveMessageListeners(""+e.getMessage(), atsOnRemoveMessageListener,2);
        }
    }

    public static void removeAllExtraListeners(AtsOnRemoveMessageListener atsOnRemoveMessageListener){
        try{
            if(mSocket.connected()){
                Set<String > allExistingListeners = ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).getStringSet(ATSConstants.KEYS.LISTENING_KEYS, null);
                if(allExistingListeners != null){
                    if(allExistingListeners.size() > 0){
                        for (String x : allExistingListeners){
                            removeRegistrationFromServerAndStopListeningAtsId(x,atsOnRemoveMessageListener);
                        }
                    }else{
                        handlerForRemoveMessageListeners("Found No Rejistered Listeners to remove",atsOnRemoveMessageListener,2);
                    }

                }
            }else{
                handlerForRemoveMessageListeners("Unable to remove as you are not connected to server", atsOnRemoveMessageListener, 2);
            }

        }catch (Exception e){
            handlerForRemoveMessageListeners(""+e.getMessage(),atsOnRemoveMessageListener, 2);
        }
    }

    public static void removeLlisteningKeyFromPrefrences(String targetAtsIdToListen) throws Exception{
        ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).getStringSet(ATSConstants.KEYS.LISTENING_KEYS,null).remove(targetAtsIdToListen);
    }

    public static void addListeningKeyInPrefrences(String targetAtsIdToListen)throws  Exception{
        Set<String >listeningKeys = ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).getStringSet(ATSConstants.KEYS.LISTENING_KEYS,null);
        if(listeningKeys == null){
            Set<String> mKey  = new HashSet<>();
            mKey.add(targetAtsIdToListen);
            ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).edit().putStringSet(ATSConstants.KEYS.LISTENING_KEYS,mKey).commit();
        }else{
            listeningKeys.add(targetAtsIdToListen);
            ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).edit().putStringSet(ATSConstants.KEYS.LISTENING_KEYS,listeningKeys).commit();
        }
    }

    public static void addTag(String tag, final AtsOnTagSetListener atsOnTagSetListener){
        try {
            if(mSocket.connected()){
                mSocket.emit(ADD_TAG, new JSONObject().put("ats_id",""+ATS.getAtsid()).put("tag",tag), new Ack() {
                    @Override
                    public void call(Object... args) {

                        ModelResultChecker modelResultChecker  = ATS.gson.fromJson(""+args[0],ModelResultChecker.class);
                        if(modelResultChecker.getResult() == 1){
                            handlerForSetTagsListeners(""+modelResultChecker.getMessage(), atsOnTagSetListener,1);
                            ModelTag modelTag = ATS.gson.fromJson(""+args[0], ModelTag.class);
                            ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).edit().putString(ATSConstants.KEYS.TAG,""+modelTag.getResponse().get(0).getTag()).commit();
                            EventBus.getDefault().post(TAG_UPDATED);
                        }else if (modelResultChecker.getResult() == 0 ){
                            handlerForSetTagsListeners(""+modelResultChecker.getMessage(), atsOnTagSetListener,2);
                        }
                    }
                });

            }else{
                handlerForSetTagsListeners("you are not connected to Server",atsOnTagSetListener, 2);
            }
        }catch (Exception e){
            handlerForSetTagsListeners("Exception: "+e.getMessage(), atsOnTagSetListener, 2);
        }
    }

    public static void removeTag (final AtsOnTagSetListener atsOnTagSetListener) {
        try{
            if(mSocket.connected()){
                mSocket.emit(REMOVE_TAG, new JSONObject().put("ats_id",""+ATS.getAtsid()), new Ack() {
                    @Override
                    public void call(Object... args) {
                        ModelResultChecker modelResultChecker = ATS.gson.fromJson(""+args[0],ModelResultChecker.class);
                        if(modelResultChecker.getResult() == 1){
                            handlerForSetTagsListeners(""+modelResultChecker.getMessage() , atsOnTagSetListener, 1);
                            ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).edit().putString(ATSConstants.KEYS.TAG,"NA").commit();
                            EventBus.getDefault().post(TAG_UPDATED);
                        }else if (modelResultChecker.getResult() == 0){
                            handlerForSetTagsListeners(""+modelResultChecker.getMessage(), atsOnTagSetListener, 2);
                        }
                    }
                });
            }else{
                handlerForSetTagsListeners("you are not connected to Server",atsOnTagSetListener, 2);
            }
        }catch (Exception e){
            handlerForSetTagsListeners("Exception: "+e.getMessage(), atsOnTagSetListener, 2);
        }
    }

    public static void listenToTag(String tag, double latitiude , double longitide, int radius, final AtsTagListener atsTagListener){
        try{
            if(mSocket.connected()){
                mSocket.emit(LISTEN_TAG, new JSONObject()
                        .put("ats_id", ""+ATS.getAtsid())
                        .put("uls",""+ATS.getAtsid()+""+tag) // ie a unique things on which he will listen tag like screen id
                        .put("tag", tag)
                        .put("latitude", latitiude)
                        .put("longitude", longitide)
                        .put("radius", radius), new Ack() {
                    @Override
                    public void call(Object... args) {
                        ModelResultChecker modelResultChecker = ATS.gson.fromJson(""+args[0],ModelResultChecker.class);
                        if(modelResultChecker.getResult() == 1){
                            ModelTagListener modelTagListener = ATS.gson.fromJson(""+args[0],ModelTagListener.class);
                            handlerForAtsTagListener(""+modelResultChecker.getMessage(), atsTagListener,1);

                            String tagListening = ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).getString(ATSConstants.KEYS.LISTEN_TAG,"NA");
                            if(!tagListening.equals("NA")){  mSocket.off(""+modelTagListener.getResponse().get(0).getUls()); }



                            ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).edit().putString(ATSConstants.KEYS.LISTEN_TAG,""+modelTagListener.getResponse().get(0).getUls()).commit();

                            mSocket.on(modelTagListener.getResponse().get(0).getUls(), new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    LOGS.i(TAG , ""+args[0]);
                                }
                            });

                        }else if(modelResultChecker.getResult() == 0){
                            handlerForAtsTagListener(""+modelResultChecker.getMessage(), atsTagListener,2);
                        }
                    }
                });
            }else{
                handlerForAtsTagListener("You are not connected with the server.", atsTagListener, 2);
            }
        }catch (Exception e){
            handlerForAtsTagListener("Exception: "+e.getMessage(), atsTagListener, 2);
        }
    }

    public static void stopListeningTag(final AtsOnTagSetListener atsOnTagSetListener){
        try{
            final String listening_tag = ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).getString(ATSConstants.KEYS.LISTEN_TAG,"NA");
            if(mSocket.connected()){
                if(!listening_tag.equals("NA")){
                    mSocket.emit(STOP_LISTEN_TAG, new JSONObject()
                            .put("ats_id", ""+ATS.getAtsid()), new Ack() {
                        @Override
                        public void call(Object... args) {
                            ModelResultChecker modelResultChecker = ATS.gson.fromJson(""+args[0],ModelResultChecker.class);
                            if(modelResultChecker.getResult() == 1){
                                mSocket.off(listening_tag);
                                ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).edit().putString(ATSConstants.KEYS.LISTEN_TAG,"NA").commit();
                                handlerForSetTagsListeners(""+modelResultChecker.getMessage(),atsOnTagSetListener,1);
                            }else{
                                handlerForSetTagsListeners(""+modelResultChecker.getMessage(),atsOnTagSetListener,2);
                            }
                        }
                    });

                }else{
                    handlerForSetTagsListeners("You are not listening to any tag",atsOnTagSetListener, 2);
                }
            }else{
                handlerForSetTagsListeners("You are not connected with server",atsOnTagSetListener,2);
            }
        }catch (Exception e){
            handlerForSetTagsListeners("Exception: "+e.getMessage(),atsOnTagSetListener,2);
        }
    }

    public static void startTrip(final String tripIdentifier, final AtsOnTripSetListener atsOnTripSetListener){
        try{
            if(mSocket.connected()){
                LOGS.e(TAG , "Connected ");
                mSocket.emit(START_TRIP, new JSONObject().put("ats_id", ""+ATS.getAtsid()).put("tripIdentifier", tripIdentifier), new Ack() {
                    @Override
                    public void call(Object... args) {
                        ModelResultChecker modelResultChecker = ATS.gson.fromJson(""+args[0],ModelResultChecker.class);
                        if(modelResultChecker.getResult() == 1){

                            LOGS.e(">>>>>>>>>>>>>. START TRIP",""+args[0]);

                            handlerForTripSetListener(""+modelResultChecker.getMessage(), atsOnTripSetListener, 1);

                        }else{
                            handlerForTripSetListener(""+modelResultChecker.getMessage(), atsOnTripSetListener,2);
                        }
                    }
                });
            }else{
                LOGS.e(TAG , "Not Connected ");
                handlerForTripSetListener("You are not connected to server", atsOnTripSetListener, 2);
            }
        }catch (Exception e){
            handlerForTripSetListener("Exception: "+e.getMessage(), atsOnTripSetListener, 2);
        }
    }

    public static void endTrip(final String tripIdentifier, final AtsOnTripSetListener atsOnTripSetListener){
        try{
            if(mSocket.connected()){
                mSocket.emit(STOP_TRIP, new JSONObject().put("ats_id", "" + ATS.getAtsid()).put("tripIdentifier", tripIdentifier), new Ack() {
                    @Override
                    public void call(Object... args) {
                        ModelResultChecker modelResultChecker = ATS.gson.fromJson(""+args[0],ModelResultChecker.class);
                        if(modelResultChecker.getResult() == 1){
                            ModelTripEnd modelTripEnd = ATS.gson.fromJson(""+args[0], ModelTripEnd.class);
                            handlerForTripSetListener(""+modelTripEnd.getResponse().get(0).getPolyline(), atsOnTripSetListener, 1);
                        }else{
                            handlerForTripSetListener(""+modelResultChecker.getMessage(), atsOnTripSetListener, 2);
                        }
                    }
                });
            }else{
                handlerForTripSetListener("Your are not connected to the Server", atsOnTripSetListener,2);
            }
        }catch (Exception e){
            handlerForTripSetListener("Exception: "+e.getMessage(), atsOnTripSetListener,2);
        }
    }

    // on add listener handlers
    private static void handerForAddMessageListener(final String message, final AtsOnAddMessageListener atsOnAddMessageListener, int type){
       if(type == 1){ // ie for on success
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsOnAddMessageListener.onRegistrationSuccess(message);
                }
            });

        }if(type == 2){ // ie for on failed
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsOnAddMessageListener.onRegistrationFailed(message);
                }
            });
        }if(type == 3 ){  // ie for on message received
           mHandler.post(new Runnable() {
               @Override
               public void run() {
                   atsOnAddMessageListener.onMessage(message);
               }
           });
        }
    }


    // on remove listener handlers
    private static void handlerForRemoveMessageListeners(final String message, final AtsOnRemoveMessageListener atsOnRemoveMessageListener, int type){
        if(type == 1){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsOnRemoveMessageListener.onRemoveSuccess(""+message);
                }
            });
        }if(type == 2){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
               atsOnRemoveMessageListener.onRemoveFailed(""+message);
                }
            });
        }
    }


    private static void handlerForSetTagsListeners(final String message , final AtsOnTagSetListener atsOnTagSetListener , int type){
        if(type == 1){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsOnTagSetListener.onSuccess(""+message);
                }
            });
        }if(type == 2 ){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsOnTagSetListener.onFailed(""+message);
                }
            });

        }
    }


    private static void handlerForAtsTagListener(final String message , final AtsTagListener atsTagListener , int type){
        if(type == 1){  // on success
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsTagListener.onSuccess(""+message);
                }
            });
        }if(type == 2){  // on failed
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsTagListener.onFailed(""+message);
                }
            });

        }if(type == 3){  // on add
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsTagListener.onAdd(""+message);
                }
            });

        }if(type == 4){ // on change
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsTagListener.onChange(""+message);
                }
            });

        }if(type == 5){  // on remove
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsTagListener.onRemove(""+message);
                }
            });

        }
    }

    private static void handlerForTripSetListener(final String message, final AtsOnTripSetListener atsOnTripSetListener, int type){
        if(type == 1){ // onSuccess
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsOnTripSetListener.onSuccess(""+message);
                }
            });

        }else if (type == 2){ // onfailed
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    atsOnTripSetListener.onFailed(""+message);
                }
            });

        }
    }

    // EMITTERS

    public  void emitDevice(  String data){
        mSocket.emit(CONNECT_DEVICE, data, new Ack() {
            @Override
            public void call(Object... args) {
                LOGS.d(TAG , ""+args[0]);
                try{
                    ATS.mBuilder.mApplication.getSharedPreferences(ATSConstants.PREFRENCES, Context.MODE_PRIVATE).edit().putString(ATSConstants.KEYS.ATS_ID,""+args[0]).commit();
                    // after saving ats_id in SP it will start listening to this ats_id all message will be deliver in this code (notification, location, message from panel . . . )
                    mSocket.off(""+args[0],onAtsIdMessage); // removing previous listening so that it will listen only one time.
                    mSocket.on(""+args[0],onAtsIdMessage);
                }catch (Exception e){
                    LOGS.e(TAG , ""+e.getMessage());
                }
            }
        });
    }


    public  void emitLocation(JSONObject jsonObject){
        mSocket.emit(LOCATION, jsonObject, new Ack() {
            @Override
            public void call(Object... args) {
//                try{ LOGS.i(TAG , ""+ATS.gson.toJson(args[0])); }
//                catch (Exception e){ LOGS.e(TAG , ""+e.getMessage()); }
            }
        });
    }


    public  static boolean isSocketConnected(){
        if(mSocket.connected()){
            return true ;
        }else{
            return false;
        }
    }

    private
    void startLocationStashEmission(){
        try{
            final List<LocationLogs> locationLogs = databaseManager.getCollcetedLogs(30);
            if(locationLogs.size() > 0 ){
                mSocket.emit(LOCATION_STASH, AppUtils.getLocationStashString(locationLogs), new Ack() {
                    @Override
                    public void call(Object... args) {
                        ModelResultChecker modelResultChecker = ATS.gson.fromJson(""+args[0], ModelResultChecker.class);
                        if(modelResultChecker.getResult() == 1){
                            List<Integer> deletedStash = databaseManager.deleteLogsStash(locationLogs) ;
                        if( deletedStash != null){
                            EventBus.getDefault().post(LOG_STASH_UPDATED);
                            startLocationStashEmission();
                        }else{
                            LOGS.e(TAG , "Failed to sync location log stash.");
                        }
                        }else{
                            LOGS.e(TAG , ""+modelResultChecker.getMessage());
                        }
                    }
                });

            }
        }catch (Exception e){
            LOGS.e(TAG , ""+e.getMessage());
        }
    }

}
