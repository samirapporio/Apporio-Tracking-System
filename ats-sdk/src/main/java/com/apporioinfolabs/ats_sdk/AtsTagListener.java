package com.apporioinfolabs.ats_sdk;

public interface AtsTagListener {
    void onSuccess(String message); // 1
    void onFailed(String message);  // 2
    void onAdd(String message);  // 3
    void onChange(String message); // 4
    void onRemove(String message); // 5
}
