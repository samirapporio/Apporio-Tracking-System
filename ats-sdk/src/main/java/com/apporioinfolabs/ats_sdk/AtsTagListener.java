package com.apporioinfolabs.ats_sdk;

import android.location.Location;

public interface AtsTagListener {
    void onSuccess(String message); // 1
    void onFailed(String message);  // 2
    void onAdd(Location location);  // 3
    void onChange(Location location); // 4
    void onRemove(Location location); // 5
}
