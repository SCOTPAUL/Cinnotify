package uk.co.paulcowie.cinnotify.networking;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
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

        if(!serverIsUp(server_ip, port)){
            Log.i(TAG, "Not sending since server is not up");
            return;
        }

        NotificationSerializer serializer = new NotificationSerializer(notification);
        byte[] transmission = serializer.getSerializedTransmission();

        try {
            InetAddress serverAddr = InetAddress.getByName(server_ip);

            try(Socket socket = new Socket(serverAddr, port);
                OutputStream out = socket.getOutputStream()) {
                out.write(transmission);
                out.flush();
            }
            catch(IOException e){
                Log.w(TAG, e.getMessage());
            }
        }
        catch(UnknownHostException e){
            Log.w(TAG, e.getMessage());
        }
    }

    private boolean serverIsUp(String ip, int port){
        try {
            SocketAddress sockaddr = new InetSocketAddress(ip, port);
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 2000;   // 2 seconds
            sock.connect(sockaddr, timeoutMs);

            return true;
        }catch(Exception ignored){
            return false;
        }

    }
}
