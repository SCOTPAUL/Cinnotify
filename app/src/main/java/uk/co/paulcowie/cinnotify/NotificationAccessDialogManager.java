package uk.co.paulcowie.cinnotify;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

/**
 * Allows for the creation of a dialog to direct the user to the Notification Access settings
 */
public class NotificationAccessDialogManager {
    private Context context;
    private AlertDialog dialog;

    public NotificationAccessDialogManager() {

    }

    public NotificationAccessDialogManager(Context context){
        this.context = context;
    }

    /**
     * Checks if the app is able to access notifications
     * @return {@code true} if app has notification access, {@code false} otherwise.
     */
    private boolean hasNotificationAccess() {
        ContentResolver contentResolver = context.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = context.getPackageName();

        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }

    /**
     * If the app does not have access to user notifications, a dialog is created which directs them
     * there. Otherwise, nothing happens.
     */
    public void popupIfAccessNeeded(){
        if(!hasNotificationAccess()){
            if(dialog != null && dialog.isShowing()){
                return;
            }
            
            dialog = buildDialog();
            dialog.show();
        }
    }

    private AlertDialog buildDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(R.string.notification_access_dialog_content)
                .setTitle(R.string.notification_access_dialog_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                context.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));

            }
        });

        builder.setCancelable(false);

        return builder.create();
    }

    public void setContext(Context context){
        this.context = context;
    }

}
