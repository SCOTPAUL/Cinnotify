package uk.co.paulcowie.cinnotify;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.io.IOException;

import uk.co.paulcowie.cinnotify.networking.NotificationSender;


public class NotificationListener extends NotificationListenerService {


    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        boolean sendable;

        try {
            AllowedNotificationAppManager allowedApps = new AllowedNotificationAppManager(getApplicationContext());
            sendable = allowedApps.canSendNotification(sbn.getPackageName());
        } catch (IOException e) {
            sendable = false;
        }

        if(sendable && isEnabled()){
            new Thread(new NotificationSender(getApplicationContext(), sbn.getNotification())).start();
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
