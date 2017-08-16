package com.example.desent.desent.fragments;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.desent.desent.R;

/**
 * Created by celine on 11/07/17.
 */

public class RegisterHousingFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private RadioGroup heatingTypeRadioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getTheme().applyStyle(R.style.AppTheme_NoActionBar_AccentColorBlue, true);

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_register_housing, container, false);

        heatingTypeRadioGroup = rootView.findViewById(R.id.radio_group_heating);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        restorePreferences();

        return rootView;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String heatType = (String) ((RadioButton) getView().findViewById(heatingTypeRadioGroup.getCheckedRadioButtonId())).getText();

        editor.putString("pref_key_heat_type", heatType);

        editor.commit();

    }

    private void restorePreferences(){

        String heatType = sharedPreferences.getString("pref_key_heat_type", "Electric (resistance)");

        switch(heatType) {

            case "Electric (resistance)":
                heatingTypeRadioGroup.check(R.id.radio_button_electric);
                break;
            case "Heat pump":
                heatingTypeRadioGroup.check(R.id.radio_button_heat_pump);
                break;
            case "Gas":
                heatingTypeRadioGroup.check(R.id.radio_button_gas);
                break;
            case "Oil":
                heatingTypeRadioGroup.check(R.id.radio_button_oil);
                break;
            case "Wood":
                heatingTypeRadioGroup.check(R.id.radio_button_wood);
                break;

        }

    }
}
