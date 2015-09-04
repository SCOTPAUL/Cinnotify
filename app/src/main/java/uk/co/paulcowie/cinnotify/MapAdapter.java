package uk.co.paulcowie.cinnotify;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by paul on 04/09/15.
 */
public class MapAdapter<K, V> extends BaseAdapter {
    private final ArrayList<Map.Entry<K, V>> data;
    private int resource;
    private int textViewResourceId;

    private static final String TAG = MapAdapter.class.getName();


    public MapAdapter(Map<K, V> map, int resource, int textViewResourceId){
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;

        data = new ArrayList<>();
        data.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Map.Entry<K, V> getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        final TextView text;

        if(convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        }
        else {
            view = convertView;
        }

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
                    "MapAdapter requires the resource ID to be a TextView", e);
        }

        Map.Entry<K, V> item = getItem(position);
        text.setText(item.toString());

        return view;
    }
}
