package uk.co.paulcowie.cinnotify.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import uk.co.paulcowie.cinnotify.R;

/**
 * Allows Activities to set their themes based on the theme option in the app's SharedPreferences
 */
public class ThemeSwitcher {

    /**
     * Sets the theme based on the {@code theme_pref_key} value
     * @param context Activity context
     * @param theme_pref_key Key of the shared preference which contains the theme choice
     * @param theme_default Default theme if the {@code theme_pref_key} can't be found.
     *                      The options are: {@code "dark"},{@code"light"}
     */
    public static void switchTheme(Context context, String theme_pref_key, String theme_default){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = preferences.getString(theme_pref_key, theme_default);

        switch(theme) {
            case "dark":
                context.setTheme(R.style.AppTheme);
                break;
            case "light":
                context.setTheme(R.style.AppThemeLight);
            default:
        }

    }
}
