package uk.co.paulcowie.cinnotify;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by paul on 03/09/15.
 */
public class AllowedNotificationAppManager {
    private static String MAP_FILE_NAME = "allowed_notification_apps.ser";

    private Map<String, Boolean> allowedAppInfo;
    private final Context context;
    private final PackageManager pm;

    public AllowedNotificationAppManager(Context context) throws IOException {
        this.context = context;
        pm = context.getPackageManager();
        loadMap();
    }

    private void saveMap() throws IOException {
        FileOutputStream fos = context.openFileOutput(MAP_FILE_NAME, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(allowedAppInfo);
    }

    @SuppressWarnings("unchecked")
    private void loadMap() throws IOException {
        FileInputStream fis = context.openFileInput(MAP_FILE_NAME);
        ObjectInputStream ois = new ObjectInputStream(fis);

        try {
            allowedAppInfo = (Map<String, Boolean>) ois.readObject();
        }
        catch(ClassNotFoundException e){
            allowedAppInfo = new ConcurrentHashMap<>();
        }
        populateMap();
    }

    private void populateMap(){
        List<ApplicationInfo> appInfo = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        // Remove old apps from map
        for(String appName : allowedAppInfo.keySet()){
            boolean found = false;

            for(ApplicationInfo app : appInfo){
                if(app.loadLabel(pm).equals(appName)){
                    found = true;
                }
            }

            if(!found){
                allowedAppInfo.remove(appName);
            }
        }

        // Add new apps to map
        for(ApplicationInfo app : appInfo){
            if(!allowedAppInfo.containsKey((String) app.loadLabel(pm))){
                allowedAppInfo.put((String) app.loadLabel(pm), false);
            }
        }
    }

    public Map<String, Boolean> getUserReadableAppMap(){
        Map<String, Boolean> userReadableMap = new HashMap<>(allowedAppInfo.size());

        String appName;
        for(String packageName : allowedAppInfo.keySet()){
            if((appName = getUserReadableFromPackageName(packageName)) != null){
                userReadableMap.put(appName, allowedAppInfo.get(packageName));
            }
        }

        return userReadableMap;
    }

    private String getUserReadableFromPackageName(String packageName){
        // https://stackoverflow.com/questions/5841161/get-application-name-from-package-name

        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : null);
    }

    public boolean canSendNotification(String packageName){
        Boolean ret = allowedAppInfo.get(packageName);
        return (ret != null) && ret;
    }

    @Override
    protected void finalize() throws Throwable{
        saveMap();
        super.finalize();
    }


}
