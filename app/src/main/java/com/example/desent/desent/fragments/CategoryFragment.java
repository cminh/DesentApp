package com.example.desent.desent.fragments;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.activities.MainActivity;
import com.example.desent.desent.R;
import com.example.desent.desent.utils.Utility;
import com.example.desent.desent.views.CircularIndicator;

/**
 * Created by celine on 29/04/17.
 */
public class CategoryFragment extends Fragment {

    protected Indicator indicator;
    protected String imgName;
    protected CircularIndicator circularIndicator;
    protected ImageView categoryImage;
    protected String categoryName;
    protected int categoryIndex;
    protected int startAngle = 270;
    protected int sweepAngle = 360;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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


        if ((indicator != null) && (categoryIndex < indicator.getAverageValues().length)) {
            values[0] = indicator.getAverageValues()[categoryIndex];
            circularIndicator.setValues(values);
            ((TextView) getView().findViewById(R.id.category_content)).setText(Utility.floatToStringNDecimals(values[0], indicator.getDecimalsNumber()) + " " + indicator.getUnit());
        }

        circularIndicator.invalidate();

    }

    public void setUp() {

        categoryImage = getView().findViewById(R.id.image_view_category);
        Resources res = getResources();
        categoryImage.setImageBitmap(BitmapFactory.decodeResource(res, res.getIdentifier(categoryName.toLowerCase(), "drawable", getActivity().getPackageName())));

        circularIndicator = getView().findViewById(R.id.category_image);
        circularIndicator.setColor(indicator.getColors().get(categoryIndex));
        circularIndicator.setStartAngle(this.startAngle);
        circularIndicator.setSweepAngle(this.sweepAngle);

        if (categoryName.length() > 10){
            categoryName = categoryName.substring(0,9) + ".";
        }

        ViewGroup.LayoutParams tempParams = circularIndicator.getLayoutParams();
        tempParams.height = Utility.dpToPx(60);
        tempParams.width = Utility.dpToPx(60);
        circularIndicator.setLayoutParams(tempParams);

        circularIndicator.setMaxValue(indicator.getMaxValue());
        circularIndicator.setDecimalsNumber(indicator.getDecimalsNumber());

        ((TextView) getView().findViewById(R.id.category_name)).setText(categoryName);
        ((TextView) getView().findViewById(R.id.category_name)).setTextColor(indicator.getColors().get(categoryIndex));
        ((TextView) getView().findViewById(R.id.category_content)).setTextColor(indicator.getColors().get(categoryIndex));

    }
}

