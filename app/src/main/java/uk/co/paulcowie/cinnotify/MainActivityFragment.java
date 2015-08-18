package uk.co.paulcowie.cinnotify;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        final Button notification_button = (Button) view.findViewById(R.id.notify_button);

        notification_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification(view, "Foo", "Bar");
            }
        });

        return view;
    }

    /**
     * Sends a notification to the system for testing.
     * @param view View of the fragment
     * @param title Title of the notification
     * @param content Text content of the notification
     */
    private void sendNotification(View view, String title, String content){
        Context context = view.getContext();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle(title)
                .setContentText(content);


        int notificationId = 1;
        NotificationManager notifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMgr.notify(notificationId, builder.build());
    }
}
