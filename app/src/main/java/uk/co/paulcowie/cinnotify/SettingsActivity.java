package uk.co.paulcowie.cinnotify;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.regex.Pattern;

import uk.co.paulcowie.cinnotify.util.ThemeSwitcher;

/**
 * Created by paul on 19/08/15.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSwitcher.switchTheme(this, "pref_theme", "dark");
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
                            if(!valid){
                                displayErrorToast(newValue + " is not a valid port.");
                            }
                            return valid;
                        }
                    }
            );
        }

        private void displayErrorToast(String errorMsg){
            Context context = getActivity().getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, errorMsg, duration);
            toast.show();
        }

    }
}
