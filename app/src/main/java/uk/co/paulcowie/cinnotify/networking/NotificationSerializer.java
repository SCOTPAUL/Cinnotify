package uk.co.paulcowie.cinnotify.networking;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
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
    private String senderPackage;

    public NotificationSerializer(String senderPackage, Context context, Notification notification){
        this.notification = notification;
        this.context = context;
        this.senderPackage = senderPackage;
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
     * @param icon The icon to be serialized
     * @return a base64 encoded bitmap representation of the image, or null if it doesn't exist
     */
    private String serializeNotificationIcon(Bitmap icon){
        String b64Icon = null;

        if(icon != null) {
            int size = icon.getByteCount();
            ByteBuffer bf = ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);
            icon.copyPixelsToBuffer(bf);

            byte[] bytes = bf.array();

            b64Icon = Base64.encodeToString(bytes, Base64.NO_WRAP);
        }

        return b64Icon;
    }

    private Bitmap getIcon(int imageRid){
        Context packageContext;

        try {
            packageContext = context.createPackageContext(senderPackage, 0);
        }
        catch (PackageManager.NameNotFoundException e){
            return null;
        }

        return BitmapFactory.decodeResource(packageContext.getResources(), imageRid);

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
        String b64Icon = null;


        int imageRid = extras.getInt(Notification.EXTRA_SMALL_ICON);
        Bitmap icon = getIcon(imageRid);
        if (icon != null) {
            icon = icon.copy(Bitmap.Config.ARGB_8888, false);
            b64Icon = serializeNotificationIcon(icon);
        }

        JSONObject obj = new JSONObject();

        try {
            obj.put("title", title);
            obj.put("desc", text);

            if(b64Icon != null) {
                JSONObject iconData = new JSONObject()
                        .put("width", icon.getWidth())
                        .put("height", icon.getHeight())
                        .put("hasAlpha", icon.hasAlpha())
                        .put("rowLength", icon.getRowBytes())
                        .put("b64data", b64Icon);

                obj.put("icon", iconData);

                icon.recycle();
            }

        } catch (JSONException e) {
            return "";
        }

        return obj.toString();
    }

}
