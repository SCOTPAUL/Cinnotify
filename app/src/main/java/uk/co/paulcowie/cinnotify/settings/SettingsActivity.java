package uk.co.paulcowie.cinnotify.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Pattern;

import uk.co.paulcowie.cinnotify.AllowedNotificationManager;
import uk.co.paulcowie.cinnotify.R;

/**
 * An {@link AppCompatActivity} which shows and manages user preferences, and handles error messages
 * if invalid values are set.
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            findPreference("pref_ip").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean valid = Pattern.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$", (String) newValue);
                            if (!valid) {
                                displayErrorToast(newValue + " is not a valid IP.");
                            }
                            return valid;
                        }
                    }
            );

            findPreference("pref_port").setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean valid = Pattern.matches("^(?:[0-9]{1,5})$", (String) newValue);
                            if (!valid) {
                                displayErrorToast(newValue + " is not a valid port.");
                            }
                            return valid;
                        }
                    }
            );

            Preference allowedAppsPref = findPreference("allowed_apps_activity");
            final Intent intent = new Intent(getActivity(), AllowedAppsActivity.class);
            if (allowedAppsPref != null) {
                allowedAppsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(intent);
                        return false;
                    }
                });
            }
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View view = super.onCreateView(inflater, container, savedInstanceState);

            if(view != null){
                ListView lv = (ListView) view.findViewById(android.R.id.list);
                lv.setPadding(0, 0, 0, 0);
            }

            return view;
        }

        private void displayErrorToast(String errorMsg){
            Context context = getActivity().getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, errorMsg, duration);
            toast.show();
        }

    }
}