package com.example.desent.desent.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by magnust on 27.06.2017.
 */

public class PreferencesManager extends PreferenceActivity {

    private static final String LOGG = "PreferencesManager";
    private EditTextPreference editTextPref;

    public PreferencesManager() {
        Log.i(LOGG,"inside Constructor");
        editTextPref = (EditTextPreference) findPreference("pref_key_personal_name");
        Log.i(LOGG,"before setSummary()");
        setSummary();
        Log.i(LOGG,"setSummary()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setSummary() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        /*
        String summaryName = sharedPref.getString("pref_key_personal_name", "");
        if(summaryName.equals("")){
            Toast.makeText(getApplicationContext(), "No name summary",
                    Toast.LENGTH_SHORT).show();
        }else{
            editTextPref.setSummary("Success");
        }*/
    }
}
