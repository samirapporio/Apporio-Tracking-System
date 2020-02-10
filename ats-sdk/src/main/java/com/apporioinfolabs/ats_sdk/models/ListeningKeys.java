package com.apporioinfolabs.ats_sdk.models;

public class ListeningKeys {
    int _id;
    String _lkey;


    public ListeningKeys(){   }
    public ListeningKeys(int id, String _lkey){
        this._id = id;
        this._lkey = _lkey;
    }

    public ListeningKeys(String _lkey){

        this._lkey = _lkey;
    }


    public int get_id() {
        return this._id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_lkey() {
        return this._lkey;
    }
}
