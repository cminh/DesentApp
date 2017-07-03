package com.example.desent.desent.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.activities.MainActivity;
import com.example.desent.desent.R;
import com.example.desent.desent.utils.Utility;
import com.example.desent.desent.views.CircularIndicator;

import java.util.ArrayList;

/**
 * Created by celine on 29/04/17.
 */
public class CategoryFragment extends Fragment {

    protected Indicator category;
    protected Indicator indicator;
    protected String imgName;
    protected CircularIndicator circularIndicator;
    protected String categoryName;
    protected int categoryIndex;
    protected int startAngle = 270;
    protected int sweepAngle = 360;
    protected MainActivity.ActiveView activeView;

    public MainActivity.ActiveView getActiveView() {
        return activeView;
    }

    public void setActiveView(MainActivity.ActiveView activeView) {
        this.activeView = activeView;
        refresh();
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public int getCategoryIndex() {
        return categoryIndex;
    }

    public void setCategoryIndex(int categoryIndex) {
        this.categoryIndex = categoryIndex;
    }

    public Indicator getCategory() {
        return category;
    }

    public void setCategory(Indicator category) {
        this.category = category;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        return view;
    }

    public void refresh(){

        float[] values = new float[1]; //TODO: change

        switch (activeView) {

            case TODAY:

                if ((indicator != null) && (categoryIndex < indicator.getDailyValues().length)) {
                    values[0] = indicator.getDailyValues()[categoryIndex];
                    circularIndicator.setValues(values);
                    ((TextView) getView().findViewById(R.id.category_content)).setText(Utility.floatToStringNDecimals(values[0], indicator.getDecimalsNumber()) + " " + indicator.getUnit());
                }
                break;

            case WEEK:

                if ((indicator != null) && (categoryIndex < indicator.getWeeklyValues().size())) {
                    values[0] = indicator.calculateWeekAverage()[categoryIndex];
                    circularIndicator.setValues(values);
                    ((TextView) getView().findViewById(R.id.category_content)).setText(Utility.floatToStringNDecimals(values[0], indicator.getDecimalsNumber()) + " " + indicator.getUnit());
                }
                break;

            case MONTH:

                if ((indicator != null) && (categoryIndex < indicator.getMonthlyValues().size())) {
                    values[0] = indicator.calculateMonthAverage()[categoryIndex];
                    circularIndicator.setValues(values);
                    ((TextView) getView().findViewById(R.id.category_content)).setText(Utility.floatToStringNDecimals(values[0], indicator.getDecimalsNumber()) + " " + indicator.getUnit());
                }
                break;
        }

        circularIndicator.invalidate();

    }

    public void setUp() {
        circularIndicator = getView().findViewById(R.id.category_image);
        circularIndicator.setColors(category.getColors());
        circularIndicator.setImgName(category.getName().toLowerCase());
        circularIndicator.setStartAngle(this.startAngle);
        circularIndicator.setSweepAngle(this.sweepAngle);

        categoryName = category.getName();
        if (categoryName.length() > 10){
            categoryName = categoryName.substring(0,9) + ".";
        }

        ViewGroup.LayoutParams tempParams = circularIndicator.getLayoutParams();
        tempParams.height = Utility.dpToPx(60);
        tempParams.width = Utility.dpToPx(60);
        circularIndicator.setLayoutParams(tempParams);

        circularIndicator.setFormat(CircularIndicator.Format.CIRCLE_IMG);
        circularIndicator.setMaxValue(indicator.getMaxValue());
        circularIndicator.setDecimalsNumber(indicator.getDecimalsNumber());

        ((TextView) getView().findViewById(R.id.category_name)).setText(categoryName);
        ((TextView) getView().findViewById(R.id.category_name)).setTextColor(category.getColors().get(0));
        ((TextView) getView().findViewById(R.id.category_content)).setTextColor(category.getColors().get(0));


    }
}

