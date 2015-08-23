package uk.co.paulcowie.cinnotify;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import uk.co.paulcowie.cinnotify.networking.NotificationSender;


public class NotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        new Thread(new NotificationSender(getApplicationContext(), sbn.getNotification())).start();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        // Do nothing
    }
}
