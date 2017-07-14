package com.example.desent.desent.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.desent.desent.R;

/**
 * Created by celine on 11/07/17.
 */

public class RegisterActivityFragment extends Fragment {

    private EditText ageTextView;
    private EditText weightTextView;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getTheme().applyStyle(R.style.AppTheme_NoActionBar_AccentColorOrange, true);

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_register_activity, container, false);

        ageTextView = rootView.findViewById(R.id.age);
        weightTextView = rootView.findViewById(R.id.weight);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        restorePreferences();
        return rootView;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();


        SharedPreferences.Editor editor = sharedPreferences.edit();

        //TODO: gender
        editor.putString("pref_key_personal_age", String.valueOf(ageTextView.getText()));
        editor.putString("pref_key_personal_weight", String.valueOf(weightTextView.getText()));

        editor.commit();

    }

    private void restorePreferences(){

        //TODO: gender
        ageTextView.setText(sharedPreferences.getString("pref_key_personal_age", ""), TextView.BufferType.EDITABLE);
        weightTextView.setText(sharedPreferences.getString("pref_key_personal_weight", ""), TextView.BufferType.EDITABLE);

    }
}
