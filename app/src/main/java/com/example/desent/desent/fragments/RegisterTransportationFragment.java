package com.example.desent.desent.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.desent.desent.R;

/**
 * Created by celine on 11/07/17.
 */

public class RegisterTransportationFragment extends Fragment {

    private CheckBox carOwner;
    private Spinner carFuelSpinner;
    private EditText priceTextView;
    private EditText averageConsumptionTextView;
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

        carFuelSpinner = rootView.findViewById(R.id.spinner_car_fuel);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.pref_car_fuel_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        carFuelSpinner.setAdapter(adapter);

        priceTextView = rootView.findViewById(R.id.car_price);
        averageConsumptionTextView = rootView.findViewById(R.id.car_fuel_consumption);
        ownershipPeriodTextView = rootView.findViewById(R.id.car_ownership_period);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        restorePreferences();
        carOwner.callOnClick();

        return rootView;
    }

    public void onCheckboxClicked(CompoundButton compoundButton, boolean b) {

        carFuelSpinner.setEnabled(b);
        priceTextView.setEnabled(b);
        averageConsumptionTextView.setEnabled(b);
        ownershipPeriodTextView.setEnabled(b);

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();


        SharedPreferences.Editor editor = sharedPreferences.edit();

        //TODO: fuel type
        editor.putBoolean("pref_key_car_owner", carOwner.isChecked());
        editor.putString("pref_key_car_price", String.valueOf(priceTextView.getText()));
        editor.putString("pref_key_car_fuel_consumption", String.valueOf(averageConsumptionTextView.getText()));
        editor.putString("pref_key_car_ownership_period", String.valueOf(ownershipPeriodTextView.getText()));

        editor.commit();

    }

    private void restorePreferences(){

        //TODO: fuel type
        carOwner.setChecked(sharedPreferences.getBoolean("pref_key_car_owner", false));
        priceTextView.setText(sharedPreferences.getString("pref_key_car_price", ""), TextView.BufferType.EDITABLE);
        averageConsumptionTextView.setText(sharedPreferences.getString("pref_key_car_fuel_consumption", ""), TextView.BufferType.EDITABLE);
        ownershipPeriodTextView.setText(sharedPreferences.getString("pref_key_car_ownership_period", ""), TextView.BufferType.EDITABLE);

    }
}
