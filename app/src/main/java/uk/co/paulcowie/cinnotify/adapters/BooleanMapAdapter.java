package uk.co.paulcowie.cinnotify.adapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by paul on 04/09/15.
 */
public class BooleanMapAdapter extends MapAdapter<String, Boolean> {
    private static final String TAG = BooleanMapAdapter.class.getName();

    private int textViewResourceId;
    private int checkboxViewResourceId;

    public BooleanMapAdapter(Map<String, Boolean> map, int resource, int textViewResourceId, int checkboxViewResourceId) {
        super(map, resource);
        this.textViewResourceId = textViewResourceId;
        this.checkboxViewResourceId = checkboxViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        final TextView text;
        final CheckBox checkBox;

        // https://github.com/android/platform_frameworks_base/blob/master/core/java/android/widget/ArrayAdapter.java
        try {
            if (textViewResourceId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (TextView) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(textViewResourceId);
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "BooleanMapAdapter requires the resource ID to be a TextView", e);
        }

        try {
            if (textViewResourceId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                checkBox = (CheckBox) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                checkBox = (CheckBox) view.findViewById(checkboxViewResourceId);
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "You must supply a resource ID for a CheckBox");
            throw new IllegalStateException(
                    "BooleanMapAdapter requires the resource ID to be a CheckBox", e);
        }


        Map.Entry<String, Boolean> item = getItem(position);
        text.setText(item.getKey());
        checkBox.setChecked(item.getValue());

        return view;

    }
}
