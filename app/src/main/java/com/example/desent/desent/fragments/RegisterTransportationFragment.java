package com.example.desent.desent.fragments;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.desent.desent.R;

/**
 * Created by celine on 11/07/17.
 */

public class RegisterTransportationFragment extends Fragment {

    private boolean isProfileValid;
    private CheckBox carOwner;
    private Spinner carSizeSpinner;
    private EditText priceTextView;
    private EditText drivingDistanceTextView;
    private EditText ownershipPeriodTextView;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getTheme().applyStyle(R.style.AppTheme_NoActionBar_AccentColorGreen, true);

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_register_transportation, container, false);

        //TODO: button
        carOwner = rootView.findViewById(R.id.checkbox_car_owner);
        carOwner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onCheckboxClicked(compoundButton, b);
            }

        });

        carSizeSpinner = rootView.findViewById(R.id.spinner_car_size);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.pref_car_size_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        carSizeSpinner.setAdapter(adapter);

        priceTextView = rootView.findViewById(R.id.car_price);
        drivingDistanceTextView = rootView.findViewById(R.id.car_driving_distance);
        ownershipPeriodTextView = rootView.findViewById(R.id.car_ownership_period);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        restorePreferences();
        carOwner.callOnClick();

        return rootView;
    }

    public void onCheckboxClicked(CompoundButton compoundButton, boolean b) {

        carSizeSpinner.setEnabled(b);
        priceTextView.setEnabled(b);
        drivingDistanceTextView.setEnabled(b);
        ownershipPeriodTextView.setEnabled(b);

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();


        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(carOwner.isChecked()){
            editor.putBoolean("pref_key_car_owner", carOwner.isChecked());
            editor.putString("pref_key_car_size", carSizeSpinner.getSelectedItem().toString());
            editor.putString("pref_key_car_price", String.valueOf(priceTextView.getText()));
            editor.putString("pref_key_car_distance", String.valueOf(drivingDistanceTextView.getText()));
            editor.putString("pref_key_car_ownership_period", String.valueOf(ownershipPeriodTextView.getText()));
        }else{
            editor.putBoolean("pref_key_car_owner", false);
            /*
            editor.putString("pref_key_car_size", ;
            editor.putString("pref_key_car_price", String.valueOf(priceTextView.getText()));
            editor.putString("pref_key_car_distance", String.valueOf(drivingDistanceTextView.getText()));
            editor.putString("pref_key_car_ownership_period", String.valueOf(ownershipPeriodTextView.getText()));
        */
        }


        editor.commit();

    }

    private void restorePreferences(){

        carOwner.setChecked(sharedPreferences.getBoolean("pref_key_car_owner", true));
        carSizeSpinner.setSelection(((ArrayAdapter<String>) carSizeSpinner.getAdapter()).getPosition(sharedPreferences.getString("pref_key_car_size", "Small")));
        priceTextView.setText(sharedPreferences.getString("pref_key_car_price", "300000"), TextView.BufferType.EDITABLE);
        drivingDistanceTextView.setText(sharedPreferences.getString("pref_key_car_distance", "8000"), TextView.BufferType.EDITABLE);
        ownershipPeriodTextView.setText(sharedPreferences.getString("pref_key_car_ownership_period", "3"), TextView.BufferType.EDITABLE);

    }
}
