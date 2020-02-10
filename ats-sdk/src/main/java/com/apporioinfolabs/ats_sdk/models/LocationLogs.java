package com.apporioinfolabs.ats_sdk.models;

public class LocationLogs {

    int _id;
    String _log;


    public LocationLogs(){   }
    public LocationLogs(int id, String _log){
        this._id = id;
        this._log = _log;
    }

    public LocationLogs(String _log){

        this._log = _log;
    }


    public int get_id() {
        return this._id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_log() {
        return this._log;
    }

}
