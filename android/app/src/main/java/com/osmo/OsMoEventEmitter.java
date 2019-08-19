package com.osmo;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Map;
import java.util.HashMap;

import javax.annotation.Nonnull;


public class OsMoEventEmitter extends ReactContextBaseJavaModule{
    @Nonnull
    @Override
    public String getName() {
        return "OsMoEventEmitter";
    }

    public OsMoEventEmitter(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void connect() {
        return;
    }

    @ReactMethod
    public void getMessageOfTheDay() {
        return;
    }


    @ReactMethod
    public void startSendingCoordinates(Boolean once) {
        return;
    }
    @ReactMethod
    public void stopSendingCoordinates() {
        return;
    }
}
