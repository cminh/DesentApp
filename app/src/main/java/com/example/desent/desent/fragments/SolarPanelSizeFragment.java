package com.example.desent.desent.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Space;

import com.example.desent.desent.R;

/**
 * Created by celine on 26/06/17.
 */

public class SolarPanelSizeFragment extends Fragment implements View.OnClickListener {

    private ViewGroup buttonsContainer;
    private Button activeButton = null;
    int buttonsSpacing;
    int buttonSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solar_panel_size, container, false);
    }

    private void selectButton(Button button) {
        if (activeButton != null) {
            activeButton.setSelected(false);
            activeButton = null;
        }

        activeButton = button;
        button.setSelected(true);
    }

    @Override
    public void onClick(View view) {
        selectButton((Button) view);
    }

    public void setUp() {
        this.buttonsContainer = (ViewGroup) getView().findViewById(R.id.solar_panel_button_container);

        buttonsSpacing = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        buttonSize = (int) getResources().getDimension(R.dimen.circular_button_size);
    }

    public void addButton(String text) {

        Button button = (Button) getActivity().getLayoutInflater().inflate(R.layout.circular_button, buttonsContainer, false);
        button.setText(text);
        button.setOnClickListener(this);
        buttonsContainer.addView(button);

        buttonsContainer.addView(new Space(getActivity()), new ViewGroup.LayoutParams(buttonsSpacing, buttonSize));

        selectButton((Button) buttonsContainer.getChildAt(0));

    }

}
