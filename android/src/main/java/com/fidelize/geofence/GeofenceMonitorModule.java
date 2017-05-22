package com.fidelize.geofence;

import android.os.Handler;
import java.util.ArrayList;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;

public class GeofenceMonitorModule extends ReactContextBaseJavaModule {

    private Handler handler;
    private ArrayList<Geofence> geofencesList;
    private ReactContext reactContext;

    public GeofenceMonitorModule(ReactApplicationContext reactContext) {
        super(reactContext);
        geofencesList = new ArrayList<Geofence>();
    }

    @Override
    public String getName() {
        return "GeofenceMonitor";
    }

    @ReactMethod
    public void addLocation(String key, Double latitude, Double longitude) {
        geofencesList.add(getGeofenceBuild(key, latitude, longitude));
    }

    @ReactMethod
    public void removeAllLocations() {
        //geofencesList.add(getGeofenceBuild(key, latitude, longitude));
    }

    private Geofence getGeofenceBuild(String key, Double latitude, Double longitude) {
        Geofence location = new Geofence.Builder()
        .setRequestId(key)

        .setCircularRegion(
            latitude,
            longitude,
            100
        )
        .setExpirationDuration(1000 * 60 * 60 * 12)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
            Geofence.GEOFENCE_TRANSITION_EXIT)
        .build();

        return location;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofencesList);
        return builder.build();
    }
}
