package uk.co.paulcowie.cinnotify;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;


public class NotificationListener extends NotificationListenerService {

    private static final String TAG = NotificationListener.class.getName();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        Bundle extras = sbn.getNotification().extras;

        String title = extras.getString(Notification.EXTRA_TITLE);
        String text = extras.getString(Notification.EXTRA_TEXT);

        Log.i(TAG, "Title: " + title + ", Text: " + text);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){

    }
}
