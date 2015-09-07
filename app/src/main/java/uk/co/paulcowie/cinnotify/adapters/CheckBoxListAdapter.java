package uk.co.paulcowie.cinnotify.adapters;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.io.IOException;

import uk.co.paulcowie.cinnotify.AllowedNotificationAppManager;
import uk.co.paulcowie.cinnotify.R;

/**
 * Created by paul on 04/09/15.
 */
public class CheckBoxListAdapter extends AppCompatActivity {
    private AllowedNotificationAppManager allowedApps;

    private static final String TAG = CheckBoxListAdapter.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_adapter);

        allowedApps = new AllowedNotificationAppManager(this);

        final MapAdapter<String, Boolean> adapter = new BooleanMapAdapter(allowedApps.getAllowedAppInfo(), R.layout.app_name_list, R.id.textView1, R.id.checkbox1, this);
        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox1);
                adapter.setItem(position, !checkBox.isChecked());
                checkBox.toggle();
                Log.v(TAG, "Set item at position " + position + " to " + checkBox.isChecked());
            }
        });
    }

    @Override
    protected void onStop(){
        try{
            allowedApps.save();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        super.onStop();
    }
}
