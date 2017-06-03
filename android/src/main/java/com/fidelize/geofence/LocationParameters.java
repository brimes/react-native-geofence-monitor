package com.fidelize.geofence;

import com.facebook.react.bridge.ReadableMap;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;


public class LocationParameters {

	private ReadableMap mParams;
	protected static final String TAG = "ReactNativeJS";

	public LocationParameters (ReadableMap params) {
		mParams = params;
	}

	protected Boolean isValid() {
	    String param = "";
	    ArrayList<String> requiredParams = new ArrayList<String>();
	    requiredParams.add("key");
	    requiredParams.add("latitude");
	    requiredParams.add("longitude");

	    Iterator<String> itr = requiredParams.iterator(); 
	    while (itr.hasNext()) { 
	        param = itr.next();
	        Log.i(TAG, "Validating " + param);
	        if (!mParams.hasKey(param)) {
	            Log.i(TAG, "Not found " + param);
	            return false;
	        }
	    }

	    return true;
	}

	@Nullable
	public String getString(String key, String defaultValue) {
		if (!mParams.hasKey(key)) {
			return defaultValue;
		}

		return mParams.getString(key);
	}
	
	@Nullable
	public Boolean getBoolean(String key, Boolean defaultValue) {
		if (!mParams.hasKey(key)) {
			return defaultValue;
		}

		return mParams.getBoolean(key);
	}

	@Nullable
	public Double getDouble(String key, Double defaultValue) {
		if (!mParams.hasKey(key)) {
			return defaultValue;
		}

		return mParams.getDouble(key);
	}
	
	@Nullable
	public int getInt(String key, int defaultValue) {
		if (!mParams.hasKey(key)) {
			return defaultValue;
		}

		return mParams.getInt(key);
	}
	
}