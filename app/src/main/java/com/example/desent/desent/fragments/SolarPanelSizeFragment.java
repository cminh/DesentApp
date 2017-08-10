package com.example.desent.desent.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Space;

import com.example.desent.desent.R;
import com.example.desent.desent.activities.MainActivity;
import com.example.desent.desent.models.Indicator;

/**
 * Created by celine on 26/06/17.
 */

public class SolarPanelSizeFragment extends Fragment implements View.OnClickListener {

    private ViewGroup buttonsContainer;
    private Button activeButton = null;
    private float[] pvSystemSizes;
    int buttonsSpacing;
    int buttonSize;
    int maxLenght = 4;
    int lenght = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solar_panel_size, container, false);
    }

    public Button getActiveButton() {
        return activeButton;
    }

    public void setActiveButton(Button activeButton) {
        this.activeButton = activeButton;
    }

    public void selectFirstButton() {
        selectButton((Button) buttonsContainer.getChildAt(0));
    }

    private void selectButton(Button button) {
        if (activeButton != null) {
            activeButton.setSelected(false);
            activeButton = null;
        }

        activeButton = button;
        button.setSelected(true);

        //TODO: don't think is a good way to do it
        MainActivity main = (MainActivity) getActivity();
        for(Indicator indicator : main.getIndicators())
            indicator.setPvSystemSize(pvSystemSizes[buttonsContainer.indexOfChild(activeButton)/2]);
        main.refreshAll();
    }

    @Override
    public void onClick(View view) {selectButton((Button) view);
    }

    public void setUp() {
        this.buttonsContainer = getView().findViewById(R.id.solar_panel_button_container);

        buttonsSpacing = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        buttonSize = (int) getResources().getDimension(R.dimen.circular_button_size);
    }

    public void addButtons(float[] pvSystemSizes){
        this.pvSystemSizes = pvSystemSizes;
        for(int i=0; i<pvSystemSizes.length; i++)
            addButton(String.valueOf(pvSystemSizes[i]) + " kW");
    }

    private void addButton(String text) {

        lenght += 1;

        if (lenght <= maxLenght) {

            Button button = (Button) getActivity().getLayoutInflater().inflate(R.layout.circular_button, buttonsContainer, false);
            button.setText(text);
            button.setOnClickListener(this);
            buttonsContainer.addView(button);

            buttonsContainer.addView(new Space(getActivity()), new ViewGroup.LayoutParams(buttonsSpacing, buttonSize));
        }

        selectButton((Button) buttonsContainer.getChildAt(0));

    }

}
