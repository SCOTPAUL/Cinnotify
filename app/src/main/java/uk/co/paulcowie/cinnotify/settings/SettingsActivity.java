package uk.co.paulcowie.cinnotify.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.regex.Pattern;

import uk.co.paulcowie.cinnotify.R;
import uk.co.paulcowie.cinnotify.networking.services.ServiceDiscovery;

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

        private void updatePasswordSummary(String password, EditTextPreference passwordPreference, Preference enabledSwitch){
            EditText passwordEditText = passwordPreference.getEditText();
            String maskedSummary = passwordEditText.getTransformationMethod().getTransformation(password, passwordEditText).toString();
            passwordPreference.setSummary(maskedSummary);
            enabledSwitch.setEnabled(!TextUtils.isEmpty(password));
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Context context = getActivity().getApplicationContext();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String password = preferences.getString("pref_encryption_password", "");
            final Preference encryptionSwitch = findPreference("pref_encryption_enabled");
            final EditTextPreference passwordEntry = (EditTextPreference) findPreference("pref_encryption_password");

            updatePasswordSummary(password, passwordEntry, encryptionSwitch);

            passwordEntry.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            updatePasswordSummary((String) newValue, passwordEntry, encryptionSwitch);
                            return true;
                        }
                    }
            );


            final EditTextPreference ipPref = (EditTextPreference) findPreference("pref_ip");
            ipPref.setSummary(ipPref.getText());

            ipPref.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean valid = Pattern.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$", (String) newValue);
                            if (!valid) {
                                displayErrorToast(newValue + " is not a valid IP.");
                            }
                            else {
                                ipPref.setSummary((String) newValue);
                            }
                            return valid;
                        }
                    }
            );

            final EditTextPreference portPref = (EditTextPreference) findPreference("pref_port");
            portPref.setSummary(portPref.getText());
            portPref.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean valid = Pattern.matches("^(?:[0-9]{1,5})$", (String) newValue);
                            if (!valid) {
                                displayErrorToast(newValue + " is not a valid port.");
                            }
                            else {
                                portPref.setSummary((String) newValue);
                            }
                            return valid;
                        }
                    }
            );



            Preference allowedAppsPref = findPreference("allowed_apps_activity");
            final Intent allowedAppsIntent = new Intent(getActivity(), AllowedAppsActivity.class);
            if (allowedAppsPref != null) {
                allowedAppsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(allowedAppsIntent);
                        return false;
                    }
                });
            }

            Preference serviceDiscoveryPref = findPreference("service_discovery_activity");
            final Intent serviceDiscoveryIntent = new Intent(getActivity(), ServiceDiscoveryActivity.class);
            if(serviceDiscoveryPref != null){
                serviceDiscoveryPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(serviceDiscoveryIntent);
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
