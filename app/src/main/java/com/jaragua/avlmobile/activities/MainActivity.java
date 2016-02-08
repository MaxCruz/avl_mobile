package com.jaragua.avlmobile.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.entities.FrameLocation;
import com.jaragua.avlmobile.utils.Constants;

public class MainActivity extends AppCompatActivity {

    private LocalBroadcastManager localBroadcastManager;
    private TextView motive;
    private TextView date;
    private TextView latitude;
    private TextView longitude;
    private TextView altitude;
    private TextView speed;
    private TextView course;
    private TextView distance;
    private TextView accuracy;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.LocationService.BROADCAST)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null && bundle.containsKey(Constants.LocationService.MOTIVE) &&
                        bundle.containsKey(Constants.LocationService.FRAME)) {
                    FrameLocation frame = bundle.getParcelable(Constants.LocationService.FRAME);
                    if (frame == null) return;
                    motive.setText(bundle.getString(Constants.LocationService.MOTIVE));
                    date.setText(frame.getGpsDate().toString());
                    latitude.setText(String.valueOf(frame.getLatitude()));
                    longitude.setText(String.valueOf(frame.getLongitude()));
                    altitude.setText(String.valueOf(frame.getAltitude()));
                    speed.setText(String.valueOf(frame.getSpeed()));
                    course.setText(String.valueOf(frame.getBearing()));
                    distance.setText(String.valueOf(frame.getDistance()));
                    accuracy.setText(String.valueOf(frame.getAccuracy()));
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localBroadcastManager = LocalBroadcastManager.getInstance(getBaseContext());
        setContentView(R.layout.activity_main);
        date = (TextView) findViewById(R.id.date);
        motive = (TextView) findViewById(R.id.motive);
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        altitude = (TextView) findViewById(R.id.altitude);
        speed = (TextView) findViewById(R.id.speed);
        course = (TextView) findViewById(R.id.course);
        distance = (TextView) findViewById(R.id.distance);
        accuracy = (TextView) findViewById(R.id.accuracy);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.LocationService.BROADCAST);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.MainActivity.MOTIVE, motive.getText().toString());
        outState.putString(Constants.MainActivity.DATE, date.getText().toString());
        outState.putString(Constants.MainActivity.LATITUDE, latitude.getText().toString());
        outState.putString(Constants.MainActivity.LONGITUDE, longitude.getText().toString());
        outState.putString(Constants.MainActivity.ALTITUDE, altitude.getText().toString());
        outState.putString(Constants.MainActivity.SPEED, speed.getText().toString());
        outState.putString(Constants.MainActivity.COURSE, course.getText().toString());
        outState.putString(Constants.MainActivity.DISTANCE, distance.getText().toString());
        outState.putString(Constants.MainActivity.ACCURACY, accuracy.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        motive.setText(savedInstanceState.getString(Constants.MainActivity.MOTIVE));
        date.setText(savedInstanceState.getString(Constants.MainActivity.DATE));
        latitude.setText(savedInstanceState.getString(Constants.MainActivity.LATITUDE));
        longitude.setText(savedInstanceState.getString(Constants.MainActivity.LONGITUDE));
        altitude.setText(savedInstanceState.getString(Constants.MainActivity.ALTITUDE));
        speed.setText(savedInstanceState.getString(Constants.MainActivity.SPEED));
        course.setText(savedInstanceState.getString(Constants.MainActivity.COURSE));
        distance.setText(savedInstanceState.getString(Constants.MainActivity.DISTANCE));
        accuracy.setText(savedInstanceState.getString(Constants.MainActivity.ACCURACY));
    }
}
