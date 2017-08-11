package com.example.desent.desent.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
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
    private boolean updateEfficientSummary = true;
    private String keyUpdate = "";

    //Table for Peter
    private CharSequence[] entries =          {"Electric (resistance)", "Heat pump" , "Gas"    , "Oil"   , "Wood"   };
    private CharSequence[] entryValues =      {"Electric (resistance)", "Heat pump" , "Gas"    , "Oil"   , "Wood"   };
    private String[] systemEfficiency =       {"1"                    , "2"         , "3"      , "4"     , "5"      };
    private String[] defaultValueNOK =        {"1"                    , "1"         , "8"      , "8"     , "4"      };
    private String[] defaultValueEUR =        {"20.6"                 , "20.6"      , "0.9"    , "1.1"   , "0.3"    };
    private String[] unitsNOK =               {"NOK/kWh"              , "NOK/kWh"   , "NOK/m3" , "NOK/l" , "NOK/kg" };
    private String[] unitsEUR =               {"Cent/kWh"             , "Cent/kWh"  , "EUR/m3" , "EUR/l" , "EUR/kg" };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource.
        addPreferencesFromResource(R.xml.preferences);
        vehicleCost = new VehicleCost(getActivity());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences (getActivity());
        Log.i(TAG,"1");

        ListPreference listPreferenceCategory = (ListPreference) findPreference("pref_key_heat_type");
        if (listPreferenceCategory != null) {
            listPreferenceCategory.setEntries(entries);
            listPreferenceCategory.setEntryValues(entryValues);
            listPreferenceCategory.setDefaultValue(entries[0]);
        }
        updatePreferenceSummary(); // Must be exec. after initiation of list pref.

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

    private void updatePreferenceSummary() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences (getActivity());
        Map<String, ?> preferencesMap = sharedPreferences.getAll();
        updateEfficientSummary = true;
        for (Map.Entry<String, ?> entry : preferencesMap.entrySet()) {

            Preference currentPref = findPreference(entry.getKey());

            if (currentPref != null ){

                String currentValue = entry.getValue().toString();

                if(currentPref.getKey().equals("pref_key_heat_type")){
                    //Preference efficiency = findPreference("pref_key_heat_efficiency");
                    //efficiency.setSummary(systemEfficiency[getInt(currentValue)]);
                    EditTextPreference heatEff = (EditTextPreference) findPreference("pref_key_heat_efficiency");
                    EditTextPreference heatFuel = (EditTextPreference) findPreference("pref_key_heat_fuel_cost");
                    heatEff.setText(systemEfficiency[getInt(currentValue)]);
                    heatFuel.setText(defaultValueNOK[getInt(currentValue)]);

                    heatEff.setSummary(systemEfficiency[getInt(currentValue)]);
                    heatFuel.setSummary(defaultValueNOK[getInt(currentValue)] + " " + unitsNOK[getInt(currentValue)]);
                    heatFuel.setTitle("Fuel cost (" + unitsNOK[getInt(currentValue)] + ")");

                    updateEfficientSummary = false;
                    // TODO: Create method for setting summary, avoiding setting it in the update method?


                   /* SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("pref_key_heat_efficiency",systemEfficiency[getInt(currentValue)]);
                    Log.i(TAG,"System efficiency " + systemEfficiency[getInt(currentValue)]);
                    editor.commit();
                    /*Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                    */
                }

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
                        if(currentPref.getKey().equals("pref_key_heat_efficiency") && !updateEfficientSummary){
                            //Nothing
                        }else if (currentPref.getKey().equals("pref_key_heat_fuel_cost") && !updateEfficientSummary){

                        }else{
                            currentPref.setSummary(currentValue);

                        }

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
        keyUpdate = key;
        updatePreferenceSummary();

        setDefaultSummary(sharedPreferences);

    }



    private int getInt(String str){
        int res = 10; // Will crash if items are added without updating this method

        switch (str){
            case "Electric (resistance)":
                res = 0;
                break;
            case "Heat pump":
                res = 1;
                break;
            case "Gas":
                res = 2;
                break;
            case "Oil":
                res = 3;
                break;
            case "Wood":
                res = 4;
                break;
        }
        return res;
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