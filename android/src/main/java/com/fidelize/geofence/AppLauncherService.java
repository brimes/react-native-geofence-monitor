package com.fidelize.geofence;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class AppLauncherService extends IntentService {

    private static final String TAG = "ReactNativeJS";
    protected Context mContext;
    protected AppLifecycleFacade mAppLifecycleFacade;
    protected AppLaunch mAppLaunchHelper;

    public AppLauncherService() {
        super("notificationsProxyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "New intent: ");

        final Bundle notificationData = intent.getBundleExtra("pushNotification");

        mContext = this.getApplicationContext();
    	mAppLifecycleFacade = new ReactAppLifecycleFacade();
        mAppLaunchHelper = new AppLaunch();
        openApplication();

   }

   protected void openApplication() {
       //setAsInitialNotification();
       launchOrResumeApp();
   }

   protected void launchOrResumeApp() {
       final Intent intent = mAppLaunchHelper.getLaunchIntent(mContext);
       mContext.startActivity(intent);
   }

//   protected void setAsInitialNotification() {
//       InitialNotificationHolder.getInstance().set(mNotificationProps);
//   }

}