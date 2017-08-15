package com.example.desent.desent.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.desent.desent.R;
import com.example.desent.desent.activities.MainActivity;
import com.example.desent.desent.models.Indicator;

/**
 * Created by celine on 22/06/17.
 */

public class CyclingDistanceFragment extends Fragment {

    private SeekBar seekBar;
    private TextView cyclingTextView;
    private TextView drivingTextView;

    protected int cyclingDistance = 20; //TODO:test
    protected int drivingDistance = 100; //TODO:test

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cycling_distance, container, false);
    }

    public void refresh() {

        seekBar.setMax(cyclingDistance+drivingDistance);
        seekBar.setProgress(cyclingDistance);

        cyclingTextView.setText(String.valueOf(cyclingDistance) + " km");
        drivingTextView.setText(String.valueOf(drivingDistance) + " km");

        //TODO: don't think is a good way to do it
        MainActivity main = (MainActivity) getActivity();
        for(Indicator indicator : main.getIndicators()) {
            indicator.setCyclingDistance((float) cyclingDistance);
            indicator.setDrivingDistance((float) drivingDistance);
        }
        main.refreshAll();

    }

    public void setUp() {

        seekBar = getView().findViewById(R.id.seekbar_cycling_distance);
        cyclingTextView = getView().findViewById(R.id.text_view_cycling_distance);
        drivingTextView = getView().findViewById(R.id.text_view_driving_distance);

        seekBar.setMax(cyclingDistance+drivingDistance);
        seekBar.setProgress(cyclingDistance);

        cyclingTextView.setText(String.valueOf(cyclingDistance) + " km");
        drivingTextView.setText(String.valueOf(drivingDistance) + " km");

        //TODO: don't think is a good way to do it
        MainActivity main = (MainActivity) getActivity();
        for(Indicator indicator : main.getIndicators()) {
            indicator.setCyclingDistance((float) cyclingDistance);
            indicator.setDrivingDistance((float) drivingDistance);
        }
        main.refreshAll();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cyclingDistance = progress;
                drivingDistance = seekBar.getMax() - progress;

                cyclingTextView.setText(String.valueOf(cyclingDistance) + " km");
                drivingTextView.setText(String.valueOf(drivingDistance) + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getActivity(), "Estimate your values with a different cycling distance", Toast.LENGTH_SHORT).show(); //TODO: test
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //TODO: don't think is a good way to do it
                MainActivity main = (MainActivity) getActivity();
                for(Indicator indicator : main.getIndicators()) {
                    indicator.setCyclingDistance((float) cyclingDistance);
                    indicator.setDrivingDistance((float) drivingDistance);
                }
                main.refreshAll();

            }
        });
    }
}
