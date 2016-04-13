package com.jaragua.avlmobile.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.entities.Schedule;
import com.jaragua.avlmobile.services.EvacuationService;
import com.jaragua.avlmobile.services.LocationService;
import com.jaragua.avlmobile.utils.Constants;
import com.jaragua.avlmobile.utils.ScheduleService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @Bind(R.id.progress)
    protected ProgressBar progress;
    @Bind(R.id.version)
    protected TextView version;
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };
    private Activity context;
    private SharedPreferences settings;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.context = this;
        ButterKnife.bind(context);
        settings = getSharedPreferences(Constants.PREFERENCES, 0);
        this.gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
        try {
            version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e1) {
            version.setText("?");
        }
        new InitializeTask().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String request[], @NonNull int[] result) {
        switch (requestCode) {
            case Constants.SplashActivity.REQUEST_PERMISSIONS: {
                int granted = 0;
                for (int permissionResult : result) {
                    if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                        granted++;
                    }
                }
                if (granted == permissions.length) {
                    new InitializeTask().execute();
                } else {
                    Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    public class InitializeTask extends AsyncTask<Void, Integer, Void> {

        private int i = 0;

        @Override
        protected void onPreExecute() {
            int hasPermissions = 0;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
                    hasPermissions++;
            }
            if (hasPermissions != permissions.length) {
                ActivityCompat.requestPermissions(context, permissions,
                        Constants.SplashActivity.REQUEST_PERMISSIONS);
                this.cancel(true);
            } else {
                String scheduleString = settings.getString(Constants.SharedPreferences.SCHEDULE,
                        Constants.LocationService.DEFAULT_SCHEDULE);
                Schedule schedule = gson.fromJson(scheduleString, Schedule.class);
                if (schedule != null) {
                    ScheduleService scheduleService = new ScheduleService(getApplicationContext(), schedule.getDays());
                    scheduleService.schedule(LocationService.class);
                } else {
                    startService(new Intent(getApplicationContext(), LocationService.class));
                }
                startService(new Intent(getApplicationContext(), EvacuationService.class));
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (i < 100) {
                i++;
                publishProgress(i);
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int i = values[0];
            progress.setProgress(i);
        }

        @Override
        protected void onPostExecute(Void param) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }

    }

}
