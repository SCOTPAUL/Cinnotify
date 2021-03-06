package uk.co.paulcowie.cinnotify.util;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import java.util.concurrent.atomic.AtomicInteger;

import uk.co.paulcowie.cinnotify.R;

/**
 * Wrapper around {@code NotificationCompat.Builder} which makes it easier to construct simple
 * notifications for testing
 */
public class Notifier {
    private static AtomicInteger notifierId = new AtomicInteger();
    private int id;
    private NotificationManager notificationManager;
    private Context context;

    public Notifier(Context context){
        this.context = context;
        notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.id = notifierId.getAndIncrement();
    }

    /**
     * Sends a notification to the system for testing.
     * @param title Title of the notification
     * @param content Text content of the notification
     */
    public void sendNotification(String title, String content){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content);

        notificationManager.notify(id, builder.build());
    }
}
