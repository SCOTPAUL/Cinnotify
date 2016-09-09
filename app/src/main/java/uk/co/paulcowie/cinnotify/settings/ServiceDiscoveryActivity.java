package uk.co.paulcowie.cinnotify.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import uk.co.paulcowie.cinnotify.R;
import uk.co.paulcowie.cinnotify.networking.services.ServiceDiscovery;
import uk.co.paulcowie.cinnotify.settings.adapters.ServiceDiscoveryAdapter;

/**
 * Created by paul on 09/09/16.
 */
public class ServiceDiscoveryActivity extends AppCompatActivity {
    private static final String TAG = ServiceDiscoveryActivity.class.getName();

    private ServiceDiscovery serviceDiscovery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_adapter);


        final Context context = this;
        serviceDiscovery = new ServiceDiscovery(this);


        final ListView lv = (ListView) findViewById(R.id.list);
        BaseAdapter adapter = new ServiceDiscoveryAdapter(serviceDiscovery.getServiceInfos(), R.layout.service_discovery_list, R.id.hostnameTextView, R.id.ipTextView, R.id.portTextView);
        serviceDiscovery.setAdapter(adapter);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NsdServiceInfo serviceInfo = (NsdServiceInfo) parent.getItemAtPosition(position);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("pref_ip", serviceInfo.getHost().getHostAddress());
                editor.putString("pref_port", String.valueOf(serviceInfo.getPort()));

                // commit() rather than apply() since we must ensure this changes before
                // navigating up
                editor.commit();

                NavUtils.navigateUpFromSameTask(ServiceDiscoveryActivity.this);
            }
        });

        serviceDiscovery.start();
    }

    @Override
    protected void onPause(){
        if(serviceDiscovery != null){
            serviceDiscovery.stop();
        }

        super.onPause();
    }

    @Override
    protected void onResume(){
        if(serviceDiscovery != null){
            serviceDiscovery.start();
        }

        super.onResume();
    }

    @Override
    protected void onDestroy(){
        if(serviceDiscovery != null) {
            serviceDiscovery.stop();
        }

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if(serviceDiscovery != null){
            serviceDiscovery.stop();
        }

        super.onStop();
    }


}
