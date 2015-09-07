package uk.co.paulcowie.cinnotify.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

import uk.co.paulcowie.cinnotify.AllowedNotificationAppManager;

/**
 * Created by paul on 04/09/15.
 */
public class BooleanMapAdapter extends MapAdapter<String, Boolean> {
    private static final String TAG = BooleanMapAdapter.class.getName();

    private int textViewResourceId;
    private int checkboxViewResourceId;
    private AllowedNotificationAppManager allowedApps;

    public BooleanMapAdapter(Map<String, Boolean> map, int resource, int textViewResourceId, int checkboxViewResourceId, Context context) {
        super(map, resource);

        this.allowedApps = new AllowedNotificationAppManager(context);

        super.sort(new Comparator<Map.Entry<String, Boolean>>() {
            @Override
            public int compare(Map.Entry<String, Boolean> lhs, Map.Entry<String, Boolean> rhs) {
                return allowedApps.getUserReadableFromPackageName(lhs.getKey())
                        .compareTo(allowedApps.getUserReadableFromPackageName(rhs.getKey()));
            }
        });

        this.textViewResourceId = textViewResourceId;
        this.checkboxViewResourceId = checkboxViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        final TextView text;
        final CheckBox checkBox;

        text = (TextView) view.findViewById(textViewResourceId);
        checkBox = (CheckBox) view.findViewById(checkboxViewResourceId);

        Map.Entry<String, Boolean> item = getItem(position);
        text.setText(allowedApps.getUserReadableFromPackageName(item.getKey()));
        checkBox.setChecked(item.getValue());

        return view;

    }
}
