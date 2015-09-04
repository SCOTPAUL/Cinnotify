package uk.co.paulcowie.cinnotify.adapters;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.IOException;

import uk.co.paulcowie.cinnotify.AllowedNotificationAppManager;
import uk.co.paulcowie.cinnotify.R;

/**
 * Created by paul on 04/09/15.
 */
public class CheckBoxListAdapter extends AppCompatActivity {
    private AllowedNotificationAppManager allowedApps;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_adapter);

        try {
            allowedApps = new AllowedNotificationAppManager(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new BooleanMapAdapter(allowedApps.getAllowedAppInfo(), R.layout.app_name_list, R.id.textView1, R.id.checkbox1);
        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(adapter);

    }
}
