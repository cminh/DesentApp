package com.example.desent.desent.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.desent.desent.R;
import com.example.desent.desent.models.PreferencesManager;

import java.util.Map;

/**
 * Created by magnust on 23.06.2017.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String LOGG = "SettingsFragment";
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource.
        addPreferencesFromResource(R.xml.preferences);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences (getActivity());
        updatePreferenceSummary(sharedPreferences);
    }

    private void updatePreferenceSummary(SharedPreferences sharedPreferences) {
        Map<String, ?> preferencesMap = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : preferencesMap.entrySet()) {

            Preference currentPref = findPreference(entry.getKey());

            if (currentPref != null ){

                String currentValue = entry.getValue().toString();

                switch (currentValue) {
                    case "0":
                        break;
                    case "true":
                        currentPref.setSummary("Yes");
                        break;
                    case "false":
                        currentPref.setSummary("No");
                        break;
                    default:
                        currentPref.setSummary(currentValue);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreferenceSummary(sharedPreferences);
    }
}