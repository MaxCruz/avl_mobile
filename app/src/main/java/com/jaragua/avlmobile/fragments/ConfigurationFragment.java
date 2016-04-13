package com.jaragua.avlmobile.fragments;


import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.entities.Day;
import com.jaragua.avlmobile.entities.Interval;
import com.jaragua.avlmobile.entities.Schedule;
import com.jaragua.avlmobile.utils.Constants;
import com.jaragua.avlmobile.utils.Graph;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConfigurationFragment extends Fragment {

    @Bind(R.id.editTextTimeLimit)
    protected EditText editTextTimeLimit;
    @Bind(R.id.editTextDistance)
    protected EditText editTextDistance;
    @Bind(R.id.weekSchedule)
    protected LinearLayout weekSchedule;
    @Bind(R.id.textViewNoSchedule)
    protected TextView textViewNoSchedule;
    private SharedPreferences settings;
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_configuration, container, false);
        ButterKnife.bind(this, rootView);
        settings = getActivity().getSharedPreferences(Constants.PREFERENCES, 0);
        this.gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            readParameters();
        }
    }

    private void readParameters() {
        int timeLimit = settings.getInt(Constants.SharedPreferences.TIME_LIMIT,
                Constants.LocationService.TIME_LIMIT);
        editTextTimeLimit.setText(String.valueOf(timeLimit));
        editTextTimeLimit.setKeyListener(null);
        int txDistance = settings.getInt(Constants.SharedPreferences.TX_DISTANCE,
                Constants.LocationService.TX_DISTANCE);
        editTextDistance.setText(String.valueOf(txDistance));
        editTextDistance.setKeyListener(null);
        String scheduleString = settings.getString(Constants.SharedPreferences.SCHEDULE,
                Constants.LocationService.DEFAULT_SCHEDULE);
        Schedule schedule = gson.fromJson(scheduleString, Schedule.class);
        if (schedule != null) {
            weekSchedule.removeAllViews();
            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textLayoutParams.setMargins(0, Graph.dp2px(getContext(), 10), 0, 0);
            for (Day day : schedule.getDays()) {
                Week dayOfWeek = Week.values()[day.getDay().ordinal()];
                TextView textViewDay = new TextView(getContext());
                textViewDay.setText(dayOfWeek.getResource());
                textViewDay.setLayoutParams(textLayoutParams);
                textViewDay.setTypeface(null, Typeface.BOLD);
                weekSchedule.addView(textViewDay);
                LinearLayout.LayoutParams textLayoutParamsDetail = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                textLayoutParamsDetail.setMargins(Graph.dp2px(getContext(), 30), 0, 0, 0);
                for (Interval interval : day.getIntervals()) {
                    TextView textViewInterval = new TextView(getContext());
                    String text = getContext().getString(R.string.from_to);
                    textViewInterval.setText(
                            String.format(text, interval.getStart(), interval.getStop())
                    );
                    textViewInterval.setLayoutParams(textLayoutParamsDetail);
                    weekSchedule.addView(textViewInterval);
                }
            }
        } else {
            weekSchedule.removeAllViews();
            weekSchedule.addView(textViewNoSchedule);
        }
    }

    public enum Week {

        Sunday(R.string.week_sunday),
        Monday(R.string.week_monday),
        Tuesday(R.string.week_tuesday),
        Wednesday(R.string.week_wednesday),
        Thursday(R.string.week_thursday),
        Friday(R.string.week_friday),
        Saturday(R.string.week_saturday);

        private int resource;

        Week(int resource) {
            this.resource = resource;
        }

        public int getResource() {
            return resource;
        }

    }
}
