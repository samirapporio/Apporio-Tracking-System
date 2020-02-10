package com.apporioinfolabs.ats_sdk;

public interface AtsOnAddMessageListener {
    void onRegistrationSuccess(String message);  // 1
    void onRegistrationFailed(String message);  // 2
    void onMessage(String message);  // 3
}
