package uk.co.paulcowie.cinnotify.networking;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Communicates with Cinnotify server
 */
public class NotificationSender implements Runnable {
    private static final String TAG = NotificationSender.class.getName();

    private Context context;
    private Notification notification;

    /**
     * @param context The application context
     * @param notification The notification to send to the server
     */
    public NotificationSender(Context context, Notification notification) {
        this.context = context;
        this.notification = notification;
    }

    @Override
    public void run() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String server_ip = prefs.getString("pref_ip", "");
        int port = Integer.valueOf(prefs.getString("pref_port", "0"));

        Bundle extras = notification.extras;
        String title = extras.getString(Notification.EXTRA_TITLE);
        String text = extras.getString(Notification.EXTRA_TEXT);

        try {
            InetAddress serverAddr = InetAddress.getByName(server_ip);

            try(Socket socket = new Socket(serverAddr, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){
                out.println(title);
                out.println(text);
            }
            catch(IOException e){
                Log.w(TAG, e.getMessage());
            }
        }
        catch(UnknownHostException e){
            Log.w(TAG, e.getMessage());
        }
    }
}
