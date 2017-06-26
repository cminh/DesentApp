package com.example.desent.desent.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.desent.desent.R;
import com.example.desent.desent.activities.MainActivity;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.views.CircularIndicator;

/**
 * Created by celine on 28/04/17.
 */
public class CircleFragment extends Fragment {

    protected Indicator indicator;
    protected CircularIndicator circularIndicator;

    //Sizes
    protected int initialLayoutHeight = -1;
    protected int initialLayoutWidth = -1;
    protected boolean hasBeenMeasured = false;

    //Circle category
    protected int startAngle = 270;
    protected int sweepAngle = 360;
    protected String imgName;
    protected int numberOfStates = 0;
    protected int decimalsNumber;

    protected MainActivity.ActiveView activeView;

    public MainActivity.ActiveView getActiveView() {
        return activeView;
    }

    public void setActiveView(MainActivity.ActiveView activeView) {
        this.activeView = activeView;
        refresh();
    }

    public int getDecimalsNumber() {
        return decimalsNumber;
    }

    public void setDecimalsNumber(int decimalsNumber) {
        this.decimalsNumber = decimalsNumber;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public Indicator getIndicator() {return this.indicator;}

    public void setCircularIndicator (CircularIndicator circularIndicator) {this.circularIndicator = circularIndicator;}

    public CircularIndicator getCircularIndicator() {return this.circularIndicator;}

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public int getNumberOfStates() {
        return numberOfStates;
    }

    public void setNumberOfStates(int numberOfStates) {
        this.numberOfStates = numberOfStates;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_circle, container, false);
    }

    public void refresh(){

        switch (activeView){

            case TODAY:
                circularIndicator.setValues(indicator.getDailyValues());
                break;

            case WEEK:
                circularIndicator.setValues(indicator.calculateWeekAverage());
                break;

            case MONTH:
                circularIndicator.setValues(indicator.calculateMonthAverage());
        }

        circularIndicator.invalidate();

    }

    public void setUp(){

        circularIndicator = getView().findViewById(R.id.circularIndicator);
        circularIndicator.setMaxValue(indicator.getMaxValue());
        circularIndicator.setUnit(indicator.getUnit());
        circularIndicator.setColors(indicator.getColors());
        circularIndicator.setValues(indicator.getDailyValues());
        circularIndicator.setStartAngle(this.startAngle);
        circularIndicator.setSweepAngle(this.sweepAngle);
        circularIndicator.setImgName(this.imgName);
        circularIndicator.setNumberOfStates(this.numberOfStates);
        circularIndicator.setFormat(CircularIndicator.Format.CIRCLE_IMG_TEXT);
        circularIndicator.setDecimalsNumber(indicator.getDecimalsNumber());
        circularIndicator.setLimitColor(indicator.getLimitColor());
    }
}
