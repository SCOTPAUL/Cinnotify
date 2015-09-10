package uk.co.paulcowie.cinnotify.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.IOException;

import uk.co.paulcowie.cinnotify.AllowedNotificationManager;
import uk.co.paulcowie.cinnotify.R;
import uk.co.paulcowie.cinnotify.util.ThemeSwitcher;

/**
 * The activity used in the 'Allowed Apps' section of the app settings, which allows users to set
 * which apps can send notifications to the server. This ListView is loaded asynchronously.
 */
public class AllowedAppsActivity extends AppCompatActivity {
    private AllowedNotificationManager allowedApps;

    private static final String TAG = AllowedAppsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSwitcher.switchTheme(this, "pref_theme", "dark");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_adapter);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.list_progress);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            int progressBarColor = 0x651FFFFF;
            progressBar.getIndeterminateDrawable().setColorFilter(progressBarColor, PorterDuff.Mode.SRC_IN);
            progressBar.getProgressDrawable().setColorFilter(progressBarColor, PorterDuff.Mode.SRC_IN);
        }

        final ListView lv = (ListView) findViewById(R.id.list);
        lv.setVisibility(View.GONE);
        final Context context = this;

        new AsyncTask<Void, Void, Void>() {
            MapAdapter<String, Boolean> adapter;

            @Override
            protected Void doInBackground(final Void... params) {
                allowedApps = new AllowedNotificationManager(context);

                adapter = new CheckBoxAdapter(allowedApps.getAllowedAppInfo(), R.layout.app_name_list, R.id.textView1, R.id.checkbox1, context);
                return null;
            }

            @Override
            protected void onPostExecute(final Void result){
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

                progressBar.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
            }


        }.execute();
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
