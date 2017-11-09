package com.wolff.wtracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.wolff.wtracker.R;
import com.wolff.wtracker.WTrackerServise;
import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.online.DbSchemaOnline;
import com.wolff.wtracker.online.OnlineDataLab;
import com.wolff.wtracker.tools.Debug;

import java.util.ArrayList;


/**
 * Created by wolff on 13.07.2017.
 */

public class Settings_fragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences_general);
        Preference btn_StopSrevice = findPreference("prefButtonStopService");
        btn_StopSrevice.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), WTrackerServise.class);
                getActivity().stopService(intent);
                Debug.Log("STOP", "Servise!");
                return true;
            }
        });
        Preference btn_StartSrevice = findPreference("prefButtonStartService");
        btn_StartSrevice.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), WTrackerServise.class);
                getActivity().startService(intent);
                Debug.Log("START", "Servise!");
                return true;
            }
        });
        Preference btn_CreateServerTables = findPreference("prefButtonCreateServerTables");
        btn_CreateServerTables.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new DbSchemaOnline().create_online_tables(getContext());
                return true;
            }
        });
    }

    public static Settings_fragment newInstance(){
        return new Settings_fragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Настройки");

    }
}
