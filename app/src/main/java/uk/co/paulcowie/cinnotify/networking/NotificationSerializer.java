package uk.co.paulcowie.cinnotify.networking;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import uk.co.paulcowie.cinnotify.R;

/**
 * Responsible for converting a notification into a form suitable for sending
 * over the network.
 *
 * The transmission will have 4 bytes used to store the length of the rest
 * of the transmission, followed by a notification title and description
 * in JSON
 */
public class NotificationSerializer {
    private Notification notification;
    private Context context;

    public NotificationSerializer(Context context, Notification notification){
        this.notification = notification;
        this.context = context;
    }

    /**
     * Creates a serialized transmission in the required form.
     *
     * @return byte array in the format shown in {@link NotificationSerializer}.
     * @see NotificationSerializer
     */
    public byte[] getSerializedTransmission(){
        byte[] transmissionBody;
        transmissionBody = getTransmissionFromNotification().getBytes(StandardCharsets.UTF_8);

        int transmissionSize = transmissionBody.length;
        byte[] size = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(transmissionSize).array();
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
     * Creates a base64 encoded String representation of a notification icon
     * @param imageRid The resource id of the image
     * @return a base64 encoded bitmap representation of the image, or null if it doesn't exist
     */
    private String serializeNotificationIcon(int imageRid){
        String b64Icon = null;
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), imageRid);

        if(icon != null) {
            int size = icon.getByteCount();
            ByteBuffer bf = ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);
            icon.copyPixelsToBuffer(bf);

            byte[] bytes = bf.array();
            icon.recycle();

            b64Icon = Base64.encodeToString(bytes, Base64.NO_WRAP);
        }

        return b64Icon;
    }

    /**
     * Formats the strings in JSON, as described above.
     * @return String in format described in {@link NotificationSerializer}
     * @see NotificationSerializer
     */
    private String getTransmissionFromNotification() {
        Bundle extras = notification.extras;
        String title = extras.getString(Notification.EXTRA_TITLE);
        String text = extras.getString(Notification.EXTRA_TEXT);
        int imageRid = extras.getInt(Notification.EXTRA_SMALL_ICON);
        String b64Icon = serializeNotificationIcon(imageRid);

        JSONObject obj = new JSONObject();

        try {
            obj.put("title", title);
            obj.put("desc", text);

            if(b64Icon != null) {
                obj.put("b64Icon", b64Icon);
            }

        } catch (JSONException e) {
            return "";
        }

        return obj.toString();
    }

}
