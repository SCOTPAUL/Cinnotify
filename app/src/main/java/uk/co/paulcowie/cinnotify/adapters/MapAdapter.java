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
 * A ListView adapter that can be used with {@code Map} objects. Any class extending this one must
 * override {@link #getView(int, View, ViewGroup)} to actually display a {@link View}.
 * @see Map
 */
public abstract class MapAdapter<K extends Comparable<K>, V> extends BaseAdapter {
    private final ArrayList<Map.Entry<K, V>> data;
    private final Map<K, V> map;
    private int resource;

    private static final String TAG = MapAdapter.class.getName();

    /**
     * @param map A map
     * @param resource A layout that this adapter can be used on
     */
    public MapAdapter(Map<K, V> map, int resource){
        this.resource = resource;
        this.map = map;

        data = new ArrayList<>();
        data.addAll(map.entrySet());
    }

    /**
     * Sets a sort for the {@code ListView} that this adapter is used on.
     * @param comparator Comparator to be used for sort
     */
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

    /**
     * Provides a basic view, which must be overridden with a {@code getView()} method which
     * actually displays the contents of the map.
     *
     * {@inheritDoc}
     */
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
