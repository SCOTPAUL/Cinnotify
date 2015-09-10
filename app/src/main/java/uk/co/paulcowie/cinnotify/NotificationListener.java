package uk.co.paulcowie.cinnotify;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import uk.co.paulcowie.cinnotify.networking.NotificationSender;


public class NotificationListener extends NotificationListenerService {
    private static final String TAG = NotificationListener.class.getName();


    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        boolean sendable;
        String appName;

        AllowedNotificationManager allowedApps = new AllowedNotificationManager(getApplicationContext());
        appName = allowedApps.getUserReadableFromPackageName(sbn.getPackageName());
        sendable = allowedApps.canSendNotification(sbn.getPackageName());

        if(sendable && isEnabled()){
            new Thread(new NotificationSender(getApplicationContext(), sbn.getNotification())).start();
        }
        else{
            Log.v(TAG, "App " + appName + " sent a notification which wasn't sent on");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        // Do nothing
    }

    private boolean isEnabled(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getBoolean("pref_enabled", false);
    }

}
