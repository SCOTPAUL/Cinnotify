package uk.co.paulcowie.cinnotify.settings.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.Map;

import uk.co.paulcowie.cinnotify.AllowedNotificationManager;

/**
 * A ListView adapter used to create ListViews of application names and checkboxes.
 */
public class CheckBoxAdapter extends MapAdapter<String, Boolean> {
    private static final String TAG = CheckBoxAdapter.class.getName();

    private int textViewResourceId;
    private int checkboxViewResourceId;
    private int appIconResourceId;
    private AllowedNotificationManager allowedApps;

    public CheckBoxAdapter(Map<String, Boolean> map, int resource, int appIconResourceId, int textViewResourceId, int checkboxViewResourceId, Context context) {
        super(map, resource);

        this.allowedApps = new AllowedNotificationManager(context);

        super.sort(new Comparator<Map.Entry<String, Boolean>>() {
            @Override
            public int compare(Map.Entry<String, Boolean> lhs, Map.Entry<String, Boolean> rhs) {
                return allowedApps.getUserReadableName(lhs.getKey())
                        .compareTo(allowedApps.getUserReadableName(rhs.getKey()));
            }
        });

        this.appIconResourceId = appIconResourceId;
        this.textViewResourceId = textViewResourceId;
        this.checkboxViewResourceId = checkboxViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        final TextView text;
        final CheckBox checkBox;
        final ImageView icon;

        icon = (ImageView) view.findViewById(appIconResourceId);
        text = (TextView) view.findViewById(textViewResourceId);
        checkBox = (CheckBox) view.findViewById(checkboxViewResourceId);

        Map.Entry<String, Boolean> item = getItem(position);
        icon.setImageDrawable(allowedApps.getAppIcon(getItem(position).getKey()));
        text.setText(allowedApps.getUserReadableName(item.getKey()));
        checkBox.setChecked(item.getValue());

        return view;

    }
}
