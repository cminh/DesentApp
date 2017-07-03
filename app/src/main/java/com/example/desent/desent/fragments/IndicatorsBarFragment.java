package com.example.desent.desent.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.desent.desent.R;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.views.IndicatorsBarItem;

import java.util.ArrayList;

/**
 * Created by celine on 20/06/17.
 */

public class IndicatorsBarFragment extends Fragment {

    protected ArrayList<Indicator> indicators = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = new LinearLayout(getActivity());
        view.setOrientation(LinearLayout.HORIZONTAL);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    public ArrayList<Indicator> getIndicators() {
        return indicators;
    }

    public void setIndicators(ArrayList<Indicator> indicators) {
        this.indicators = indicators;
    }

    public void addIndicator(Indicator indicator) {
        indicators.add(indicator);
    }

    public void updateIndicatorsBarItem(Indicator indicator) {
        IndicatorsBarItem indicatorsBarItem = (IndicatorsBarItem) ((ViewGroup) getView()).getChildAt(indicators.indexOf(indicator));
        indicatorsBarItem.setValue(indicator.getDailyValue(), indicator.getDecimalsNumber());

    }

    public void refresh() {
        ViewGroup indicatorsBar = (ViewGroup) getView();
        indicatorsBar.removeAllViews();

        setUp();

    }

    public void setUp() {

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = 1;

        for (Indicator indicator : indicators) {
            IndicatorsBarItem indicatorsBarItem = new IndicatorsBarItem(getActivity());
            indicatorsBarItem.setValue(indicator.getDailyValue(), indicator.getDecimalsNumber());
            indicatorsBarItem.setUnit(indicator.getUnit());
            indicatorsBarItem.setOrientation(LinearLayout.VERTICAL);
            indicatorsBarItem.setLayoutParams(lp);

            ViewGroup indicatorsBar = (ViewGroup) getView();
            indicatorsBar.addView(indicatorsBarItem);
        }
    }
}
