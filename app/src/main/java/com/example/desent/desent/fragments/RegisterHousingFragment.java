package com.example.desent.desent.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.desent.desent.R;

/**
 * Created by celine on 11/07/17.
 */

public class RegisterHousingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getTheme().applyStyle(R.style.AppTheme_NoActionBar_AccentColorBlue, true);

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_register_housing, container, false);
        return rootView;
    }
}
