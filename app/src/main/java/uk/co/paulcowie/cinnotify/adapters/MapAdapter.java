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
    private final Map<K, V> map;
    private int resource;

    private static final String TAG = MapAdapter.class.getName();

    public MapAdapter(Map<K, V> map, int resource){
        this.resource = resource;
        this.map = map;

        data = new ArrayList<>();
        data.addAll(map.entrySet());
    }

    public void sort(Comparator<Map.Entry<K, V>> comparator){
        Collections.sort(data, comparator);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Map.Entry<K, V> getItem(int position) {
        return data.get(position);
    }

    public void setItem(int position, V newValue){
        Map.Entry<K, V> entry = data.get(position);

        K key = entry.getKey();
        entry.setValue(newValue);
        map.put(key, newValue);
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
