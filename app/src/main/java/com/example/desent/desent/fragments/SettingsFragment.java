package com.example.desent.desent.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.example.desent.desent.R;

/**
 * Created by magnust on 23.06.2017.
 */

public class SettingsFragment extends PreferenceFragment {

    private final String LOGG = "SettingsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        Log.i(LOGG, "before R.xml.preferences");
        addPreferencesFromResource(R.xml.preferences);
    }

}