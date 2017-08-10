package com.example.desent.desent.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.example.desent.desent.R;
import com.example.desent.desent.models.PreferencesManager;
import com.example.desent.desent.models.VehicleCost;

import java.util.Map;

/**
 * Created by magnust on 23.06.2017.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = "SettingsFragment";
    private SharedPreferences sharedPreferences;
    private VehicleCost vehicleCost;
    private EditTextPreference costPref;
    private PreferenceCategory preferenceCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource.
        addPreferencesFromResource(R.xml.preferences);
        vehicleCost = new VehicleCost(getActivity());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences (getActivity());
        updatePreferenceSummary(sharedPreferences);
        Log.i(TAG,"1");

        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        setDefaultSummary(sharedPreferences);

        //Preferences that shall be hidden must be hidden here
        costPref = (EditTextPreference) findPreference("pref_key_car_price");
        preferenceCategory = (PreferenceCategory) findPreference("pref_category_car");
        preferenceCategory.removePreference(costPref);

        Log.i(TAG,"3");

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
                        Log.i(TAG,"2");
                        Log.i(TAG, currentPref.getKey());
                        if(currentPref.getKey().equals("pref_car_key_advanced_default")){
                            currentPref.setSummary("Custom values activated");
                        }else{
                            currentPref.setSummary("Yes");
                        }
                        break;
                    case "false":
                        if(currentPref.getKey().equals("pref_car_key_advanced_default")){
                            currentPref.setSummary("Default values activated");
                        }else{
                            currentPref.setSummary("No");
                        }
                        break;
                    default:
                        currentPref.setSummary(currentValue);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        Log.i(TAG,"5");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreferenceSummary(sharedPreferences);
        setDefaultSummary(sharedPreferences);

    }

    private void setDefaultSummary(SharedPreferences sharedPreferences){
        if(sharedPreferences.getBoolean("pref_car_key_advanced_default", false)){

        }else{
            vehicleCost = new VehicleCost(getActivity());
            Map<String, String> myMap = vehicleCost.getDefaultValueMap(true);

            Preference advancedDefaultPref1 = findPreference("pref_car_key_advanced_yearly_fee");
            Preference advancedDefaultPref2 = findPreference("pref_car_key_advanced_fuel_consumption");
            Preference advancedDefaultPref3 = findPreference("pref_car_key_advanced_fuel_price");
            Preference advancedDefaultPref4 = findPreference("pref_car_key_advanced_Interest_cost");
            Preference advancedDefaultPref5 = findPreference("pref_car_key_advanced_insurance_cost");
            Preference advancedDefaultPref6 = findPreference("pref_car_key_advanced_tire_cost");
            Preference advancedDefaultPref7 = findPreference("pref_car_key_advanced_wash_accessories_cost");
            Preference advancedDefaultPref8 = findPreference("pref_car_key_advanced_service_cost");
            Preference advancedDefaultPref9 = findPreference("pref_car_key_advanced_repair_cost");
            Preference advancedDefaultPref10 = findPreference("pref_car_key_advanced_depreciation_yr1");
            Preference advancedDefaultPref11 = findPreference("pref_car_key_advanced_depreciation_yr2");
            Preference advancedDefaultPref12 = findPreference("pref_car_key_advanced_depreciation_yr3");
            Preference advancedDefaultPref13 = findPreference("pref_car_key_advanced_depreciation_yr4");
            Preference advancedDefaultPref14 = findPreference("pref_car_key_advanced_depreciation_yr5");
            Preference advancedDefaultPref15 = findPreference("pref_car_key_advanced_depreciation_yr6");

            advancedDefaultPref1.setSummary(myMap.get("pref_car_key_advanced_yearly_fee").toString());
            advancedDefaultPref2.setSummary(myMap.get("pref_car_key_advanced_fuel_consumption").toString());
            advancedDefaultPref3.setSummary(myMap.get("pref_car_key_advanced_fuel_price").toString());
            advancedDefaultPref4.setSummary(myMap.get("pref_car_key_advanced_Interest_cost").toString());
            advancedDefaultPref5.setSummary(myMap.get("pref_car_key_advanced_insurance_cost").toString());
            advancedDefaultPref6.setSummary(myMap.get("pref_car_key_advanced_tire_cost").toString());
            advancedDefaultPref7.setSummary(myMap.get("pref_car_key_advanced_wash_accessories_cost").toString());
            advancedDefaultPref8.setSummary(myMap.get("pref_car_key_advanced_service_cost").toString());
            advancedDefaultPref9.setSummary(myMap.get("pref_car_key_advanced_repair_cost").toString());
            advancedDefaultPref10.setSummary(myMap.get("pref_car_key_advanced_depreciation_yr1").toString());
            advancedDefaultPref11.setSummary(myMap.get("pref_car_key_advanced_depreciation_yr2").toString());
            advancedDefaultPref12.setSummary(myMap.get("pref_car_key_advanced_depreciation_yr3").toString());
            advancedDefaultPref13.setSummary(myMap.get("pref_car_key_advanced_depreciation_yr4").toString());
            advancedDefaultPref14.setSummary(myMap.get("pref_car_key_advanced_depreciation_yr5").toString());
            advancedDefaultPref15.setSummary(myMap.get("pref_car_key_advanced_depreciation_yr6").toString());
        }
    }
}