package com.fidelize.geofence;

import android.content.SharedPreferences;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.graphics.Color;
import android.content.Context;


import java.util.ArrayList;
import java.util.Iterator;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.GeofencingApi;

public class GeofenceMonitorModule extends ReactContextBaseJavaModule implements
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    private ArrayList<Geofence> geofencesList;
    private ReactContext reactContext;

    protected static final String TAG = "ReactNativeJS";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences mSharedPreferences;

    public GeofenceMonitorModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        geofencesList = new ArrayList<Geofence>();
        buildGoogleApiClient();
        mGoogleApiClient.connect();

    }

    @Override
    public String getName() {
        return "GeofenceMonitor";
    }

    @ReactMethod
    public void location(ReadableMap parametersMap) {
        LocationParameters params = new LocationParameters(parametersMap);

        if (!params.isValid()) {
            Log.i(TAG, "Missing params");
            return ;
        }

//        String  key       = parametersMap.getString("key");
//        String  latitude  = parametersMap.getString("latitude");
//        String  longitude = parametersMap.getString("longitude");
//        String  radius    = parametersMap.getString("radius");
//        String  type      = parametersMap.getString("type");
//        String  title     = parametersMap.getString("title");
//        String  text      = parametersMap.getString("text");
//        Boolean openApp   = parametersMap.getBoolean("openApp");
//        Boolean vibration = parametersMap.getBoolean("vibration");
//        Boolean expireTime = parametersMap.getBoolean("vibration");

        String type = params.getString("double", "enterAndExit");
        Integer transitionType = Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT; 
        if (type == "enter") {
            transitionType = Geofence.GEOFENCE_TRANSITION_ENTER;
        }
        if (type == "exit") {
            transitionType = Geofence.GEOFENCE_TRANSITION_EXIT;
        }
        if (type == "dwell") {
            transitionType = Geofence.GEOFENCE_TRANSITION_DWELL;
        }

        geofencesList.add(new Geofence.Builder()
            .setRequestId(params.getString("key", null))
            .setCircularRegion(
                params.getDouble("latitude", null),
                params.getDouble("longitude", null),
                params.getInt("radius", 100)
            )
            .setExpirationDuration(params.getInt("expireTime", 120000))
            .setTransitionTypes(transitionType)
            .build());
    }

    @ReactMethod
    public String start() {
        if (!mGoogleApiClient.isConnected()) {
            return "Not Connected";
        }

        try {
            removeGeofences();
            LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            Log.i(TAG, "Error verify Android Manifest");
        }
        return "OK";
    }

    protected void removeGeofences() {
        LocationServices.GeofencingApi.removeGeofences(
            mGoogleApiClient,
            getGeofencePendingIntent()
        ).setResultCallback(this);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(reactContext)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
    }    

    /**
    * Runs when a GoogleApiClient object successfully connects.
    */
    @Override
    public void onConnected(Bundle connectionHint) {
       Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(reactContext, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(reactContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.e(TAG, "Connected+");

        } else {
            Log.e(TAG, "Geofence Error code " + status.getStatusCode());
        }
    }    

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofencesList);
        return builder.build();
    }
}
