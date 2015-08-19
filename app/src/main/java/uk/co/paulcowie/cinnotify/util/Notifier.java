package uk.co.paulcowie.cinnotify.util;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import uk.co.paulcowie.cinnotify.R;

/**
 * Wrapper around NotificationCompat.Builder which makes it easier to construct simple
 * notifications for testing
 */
public class Notifier {
    private static int notifierId = 0;
    private int id;
    private NotificationManager notificationManager;
    private Context context;

    public Notifier(Context context){
        this.context = context;
        notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.id = notifierId++;
    }

    /**
     * Sends a notification to the system for testing.
     * @param title Title of the notification
     * @param content Text content of the notification
     */
    public void sendNotification(String title, String content){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle(title)
                .setContentText(content);

        notificationManager.notify(id, builder.build());
    }
}
