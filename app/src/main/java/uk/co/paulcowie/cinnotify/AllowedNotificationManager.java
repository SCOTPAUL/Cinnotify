package uk.co.paulcowie.cinnotify;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages access to a {@code Map} of apps to booleans indicating whether or not they are able to
 * send notifications to a Cinnotify server. This list is loaded from a file on use, and then
 * updated and can be saved when finished with.
 *
 * IMPORTANT: Make sure {@link #save()} is called when this object is finished with
 */
public class AllowedNotificationManager {
    private static String MAP_FILE_NAME = "allowed_notification_apps.ser";

    private static boolean mapLoaded = false;
    private static Map<String, Boolean> allowedAppInfo;
    private final Context context;
    private final PackageManager pm;

    public AllowedNotificationManager(Context context) {
        this.context = context;
        pm = context.getPackageManager();
        if(!mapLoaded) loadMap();
    }

    private void saveMap() throws IOException {
        FileOutputStream fos = context.openFileOutput(MAP_FILE_NAME, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(allowedAppInfo);
    }

    @SuppressWarnings("unchecked")
    private void loadMap() {
        try {
            FileInputStream fis = context.openFileInput(MAP_FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);

            allowedAppInfo = (Map<String, Boolean>) ois.readObject();
        }
        catch(ClassNotFoundException | IOException e){
            allowedAppInfo = new ConcurrentHashMap<>();
        }

        if(allowedAppInfo == null) allowedAppInfo = new ConcurrentHashMap<>();
        populateMap();
        mapLoaded = true;
    }

    private void populateMap(){
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA);
        final Map<String, Boolean> newMap = new ConcurrentHashMap<>();

        for(ResolveInfo resInfo: pkgAppsList){
            String packageName = resInfo.activityInfo.applicationInfo.packageName;

            if(allowedAppInfo.containsKey(packageName)){
                newMap.put(packageName, allowedAppInfo.get(packageName));
            }
            else{
                newMap.put(packageName, false);
            }
        }

        allowedAppInfo = newMap;
    }

    /**
     * Returns the Application name from the package name
     * @param packageName Name of an Android application package
     * @return Application name if it exists, else null
     */
    public String getUserReadableName(String packageName){
        // https://stackoverflow.com/questions/5841161/get-application-name-from-package-name

        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        }
        catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : null);
    }

    /**
     * Returns the Application icon from the package name
     * @param packageName Name of an Android application package
     * @return Application icon if it exists, else the system default app icon
     */
    public Drawable getAppIcon(String packageName){
        try {
            return pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return ContextCompat.getDrawable(context, android.R.drawable.sym_def_app_icon);
        }
    }

    /**
     * @param packageName Name of an Android application package
     * @return If the user has allowed Cinnotify to mirror notifications from this app
     */
    public boolean canSendNotification(String packageName){
        Boolean ret = allowedAppInfo.get(packageName);
        return (ret != null) && ret;
    }

    public Map<String, Boolean> getAllowedAppInfo(){
        return allowedAppInfo;
    }

    /**
     * Saves the Map to backing storage.
     * @throws IOException If saving failed
     */
    public void save() throws IOException {
        saveMap();
    }


}
