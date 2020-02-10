package com.apporioinfolabs.ats_sdk.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.apporioinfolabs.ats_sdk.di.ApplicationContext;
import com.apporioinfolabs.ats_sdk.models.ListeningKeys;
import com.apporioinfolabs.ats_sdk.models.LocationLogs;
import com.apporioinfolabs.ats_sdk.utils.LOGS;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseManager";


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "logdatabase";
    private static final String TABLE_COLLECTED_LOGS = "logtable";
    private static final String KEY_ID = "id";
    private static final String KEY_LOG = "logdata";

    private Context context ;

    @Inject
    public DatabaseManager(@ApplicationContext Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // table for logs
        String CREATE_LOGS_TABLE = "CREATE TABLE " + TABLE_COLLECTED_LOGS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LOG + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_LOGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECTED_LOGS);
        onCreate(db);
    }


    public void addLocationLog(String location) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOG, location);
        db.insert(TABLE_COLLECTED_LOGS, null, values);
        db.close();
    }

    private int deleteLogsbyId(int log_id) {
        int deletedLog = 00;
        SQLiteDatabase db = this.getWritableDatabase();

         deletedLog = db.delete(TABLE_COLLECTED_LOGS, KEY_ID + " = ?", new String[] { String.valueOf(log_id) });

        db.close();
        return deletedLog;
    }

    public List<LocationLogs> getAllLogsFromTable() {
        List<LocationLogs> logsdata = new ArrayList<LocationLogs>();
        String selectQuery = "SELECT  * FROM " + TABLE_COLLECTED_LOGS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                logsdata.add(  new LocationLogs(Integer.parseInt(cursor.getString(0)), "" +cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        return logsdata;
    }

    public List<LocationLogs> getCollcetedLogs(int maxHeapSize){

        List<LocationLogs> requiredBunch = new ArrayList<>();
        List<LocationLogs> logsdata  = getAllLogsFromTable();

        if(logsdata.size() <= maxHeapSize){
            for(int i =0 ; i < logsdata.size() ; i++){
                requiredBunch.add(logsdata.get(i));
            }
        }else{
            for(int i =0 ; i < maxHeapSize ; i++){
                requiredBunch.add(logsdata.get(i));
            }
        }



        return requiredBunch ;
    }

    public List<Integer> deleteLogsStash(List<LocationLogs> locationLogs){
        List<Integer> deletedLogs = new ArrayList<>();
        try{
            for(int i =0 ; i < locationLogs.size() ; i++){
                deletedLogs.add(deleteLogsbyId(locationLogs.get(i).get_id()));
            }
            return deletedLogs;
        }catch (Exception e){
            LOGS.e(TAG , ""+e.getMessage());
            return null ;
        }

    }


}
