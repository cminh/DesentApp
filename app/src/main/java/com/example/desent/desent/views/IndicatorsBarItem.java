package com.example.desent.desent.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.desent.desent.R;
import com.example.desent.desent.utils.Utility;

/**
 * Fragment representing an indicator in the app's top bar
 */

public class IndicatorsBarItem extends LinearLayout {

    protected View rootView;
    protected TextView valueTextView;
    protected TextView unitTextView;
    protected int decimalsNumber;

    public IndicatorsBarItem(Context context) {
        super(context);
        init(context);
    }


    public IndicatorsBarItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public IndicatorsBarItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        rootView = inflate(context, R.layout.item_indicators_bar, this);
        valueTextView = rootView.findViewById(R.id.value_text_view);
        unitTextView = rootView.findViewById(R.id.unit_text_view);
    }

    public void setValue(float value, int decimalsNumber) {
        valueTextView.setText(Utility.floatToStringNDecimals(value, decimalsNumber));
    }

    public void setUnit(String unit) {
        unitTextView.setText(unit);
    }


}
