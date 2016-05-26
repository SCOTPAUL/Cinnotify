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
import java.util.Arrays;

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

        Socket socket = getConnection(server_ip, port);
        if(socket == null){
            Log.i(TAG, "Not sending since server is not up");
            return;
        }

        NotificationSerializer serializer = new NotificationSerializer(context, notification);
        byte[] transmission = serializer.getSerializedTransmission();

        Log.v(TAG, "Sending message " + new String(transmission));

        try(OutputStream out = socket.getOutputStream()) {
            out.write(transmission);
            out.flush();
        }
        catch(IOException e){
            Log.w(TAG, e.getMessage());
        }
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a connected socket if the server is reachable.
     * @param ip IP address of the server to connect to
     * @param port Port to connect to
     * @return A connected socket if server is reachable, null otherwise
     */
    private Socket getConnection(String ip, int port){
        try {
            SocketAddress sockaddr = new InetSocketAddress(ip, port);
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 2000;
            sock.connect(sockaddr, timeoutMs);

            return sock;
        }
        catch(Exception e){
            Log.w(TAG, "Failed to connect");
            return null;
        }

    }
}
