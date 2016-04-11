package com.jaragua.avlmobile.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConfigurationFragment extends Fragment {

    @Bind(R.id.editTextTimeLimit)
    protected EditText editTextTimeLimit;
    @Bind(R.id.editTextDistance)
    protected EditText editTextDistance;
    private SharedPreferences settings;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_configuration, container, false);
        ButterKnife.bind(this, rootView);
        settings = getActivity().getSharedPreferences(Constants.PREFERENCES, 0);
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
        int txDistance = settings.getInt(Constants.SharedPreferences.TX_DISTANCE,
                Constants.LocationService.TX_DISTANCE);
        editTextDistance.setText(String.valueOf(txDistance));

    }
}
