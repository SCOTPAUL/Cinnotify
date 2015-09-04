package uk.co.paulcowie.cinnotify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by paul on 04/09/15.
 */
public abstract class MapAdapter<K extends Comparable<K>, V> extends BaseAdapter {
    private final ArrayList<Map.Entry<K, V>> data;
    private int resource;

    private static final String TAG = MapAdapter.class.getName();


    public MapAdapter(Map<K, V> map, int resource){
        this.resource = resource;

        data = new ArrayList<>();
        data.addAll(map.entrySet());

        Collections.sort(data, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> lhs, Map.Entry<K, V> rhs) {
                return lhs.getKey().compareTo(rhs.getKey());
            }
        });
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

        if(convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        }
        else {
            view = convertView;
        }

        return view;
    }
}
