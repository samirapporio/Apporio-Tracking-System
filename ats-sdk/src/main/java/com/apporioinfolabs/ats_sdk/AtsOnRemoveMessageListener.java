package com.apporioinfolabs.ats_sdk;

public interface AtsOnRemoveMessageListener {
    void onRemoveSuccess(String message);  // 1
    void onRemoveFailed(String message);  // 2
}
