package uk.co.paulcowie.cinnotify.networking;

import android.app.Notification;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Responsible for converting a notification into a form suitable for sending
 * over the network.
 *
 * The transmission will have 4 bytes used to store the length of the rest
 * of the transmission, followed by a notification title and description
 * in the form [title:...][desc:...]. '['and ']' characters in the text body
 * will be escaped with the '\' character.
 */
public class NotificationSerializer {
    private Notification notification;

    public NotificationSerializer(Notification notification){
        this.notification = notification;
    }

    /**
     * Creates a serialized transmission in the reuired form.
     *
     * @return byte array in the format shown in {@link NotificationSerializer}.
     * @see NotificationSerializer
     */
    public byte[] getSerializedTransmission(){
        byte[] transmissionBody = getTransmissionFromNotification().getBytes(StandardCharsets.UTF_8);

        int transmissionSize = transmissionBody.length;

        byte[] size = ByteBuffer.allocate(4).putInt(transmissionSize).array();

        byte[] transmission;

        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            os.write(size);
            os.write(transmissionBody);
            transmission = os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            transmission = new byte[0];
        }

        return transmission;
    }

    /**
     * Formats the strings for [title:...][desc:...], as described above.
     * @return String in format described in {@link NotificationSerializer}
     * @see NotificationSerializer
     */
    private String getTransmissionFromNotification(){
        Bundle extras = notification.extras;
        String title = extras.getString(Notification.EXTRA_TITLE);
        String text = extras.getString(Notification.EXTRA_TEXT);

        title = addEscapeChars(title);
        title = addFormattingToTitle(title);

        text = addEscapeChars(text);
        text = addFormattingToText(text);

        return title + text;
    }

    private String addEscapeChars(String s){
        if(s == null) return "";

        return s.replace("[", "\\[").replace("]", "\\]");
    }

    private String addFormattingToTitle(String s){
        return "[title:" + s + ']';
    }

    private String addFormattingToText(String s){
        return "[desc:" + s + ']';
    }


}
