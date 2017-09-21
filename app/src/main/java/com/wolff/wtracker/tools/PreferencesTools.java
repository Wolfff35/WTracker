package com.wolff.wtracker.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by wolff on 09.08.2017.
 */

public class PreferencesTools {
    public static final boolean IS_DEBUG = true;
/*    public static final String PREFERENCE_IS_FIRST_RUN = "pref_is_first_run";
    public static final String PREFERENCE_SERVER_NAME = "pref_server_name";
    public static final String PREFERENCE_BASE_NAME = "pref_base_name";
    public static final String PREFERENCE_BASE_LOGIN = "pref_base_login";
    public static final String PREFERENCE_BASE_PASSWORD = "pref_base_password";
*/
    public String getStringPreference(Context context, String name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(name,null);
    }
    public boolean getBooleanPreference(Context context, String name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(name,false);
    }
    public void setBooleanPreference(Context context, String name,boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(name,value).apply();
    }

}
