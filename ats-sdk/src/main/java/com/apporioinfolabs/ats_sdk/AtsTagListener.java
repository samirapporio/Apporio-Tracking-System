package com.apporioinfolabs.ats_sdk;

import android.location.Location;

public interface AtsTagListener {
    void onSuccess(String message); // 1
    void onFailed(String message);  // 2
    void onAdd(Location location, String atsId);  // 3
    void onChange(Location location, String atsId); // 4
    void onRemove(Location location, String atsId); // 5
}
