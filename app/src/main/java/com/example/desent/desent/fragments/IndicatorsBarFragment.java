package com.example.desent.desent.fragments;

import android.app.AlertDialog;
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
    protected ArrayList<IndicatorsBarItem> indicatorsBarItems = new ArrayList<>();
    protected int length;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = new LinearLayout(getActivity());
        view.setOrientation(LinearLayout.HORIZONTAL);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ArrayList<Indicator> getIndicators() {
        return indicators;
    }

    public void setIndicators(ArrayList<Indicator> indicators) {
        this.indicators = indicators;
    }

    public void addIndicator(Indicator indicator) {
        if (indicators.size() < length)
            indicators.add(indicator);
    }

    public void showExplanation(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
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

        for (final Indicator indicator : indicators) {
            final IndicatorsBarItem indicatorsBarItem = new IndicatorsBarItem(getActivity());
            indicatorsBarItem.setValue(indicator.getDailyValue(), indicator.getDecimalsNumber());
            indicatorsBarItem.setUnit(indicator.getUnit());
            indicatorsBarItem.setOrientation(LinearLayout.VERTICAL);
            indicatorsBarItem.setLayoutParams(lp);
            indicatorsBarItem.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!indicator.getExplanation().equals(""))
                                showExplanation(indicator.getName(), indicator.getExplanation());
                        }
                    }
            );

            ViewGroup indicatorsBar = (ViewGroup) getView();
            indicatorsBar.addView(indicatorsBarItem);
        }
    }
}
