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
import com.jaragua.avlmobile.utils.ScheduleHandler;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This activity start the services of the application and request their respective permissions
 */
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

    /**
     * Create the activity:
     * 1. Set the layout using ButterKnife
     * 2. Set class attributes
     * 3. Show's app version
     * 4. Start initialization task
     *
     * @param savedInstanceState instance saved from previous configurations
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Set the layout
        setContentView(R.layout.activity_splash);
        this.context = this;
        ButterKnife.bind(context);
        // 2. Set class attributes
        this.settings = getSharedPreferences(Constants.PREFERENCES, 0);
        this.gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
        // 3. Show's app version
        try {
            version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e1) {
            version.setText("?");
        }
        // 4. Initialize task
        new InitializeTask().execute();
    }

    /**
     * Result received after of the request's permission
     *
     * @param requestCode Code for the permission request operation
     * @param request     Permissions answered
     * @param result      Responses from the user
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String request[], @NonNull int[] result) {
        switch (requestCode) {
            // Response from the same activity
            case Constants.SplashActivity.REQUEST_PERMISSIONS: {
                // Quantity of granted permissions
                int granted = 0;
                // Iterate permissions in search of granted
                for (int permissionResult : result) {
                    if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                        granted++;
                    }
                }
                // If the number of granted permissions is equals to the requested
                if (granted == permissions.length) {
                    // Initialize the app
                    new InitializeTask().execute();
                } else {
                    // Notify and close (this app requiere all permissions defined
                    Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    /**
     * Check the permissions and start all the services
     */
    public class InitializeTask extends AsyncTask<Void, Integer, Void> {

        private int i = 0;

        /**
         * Before execute the initialization:
         * 1. Verify the needed permissions. If not is granted, request it
         * 2. Get saved service schedule
         * 3. If a schedule is defined, start location service with this, other way, start directly
         * 4. Start evacuation service
         */
        @Override
        protected void onPreExecute() {
            //1. Verify the needed permissions
            int hasPermissions = 0;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
                    hasPermissions++;
            }
            // If not is granted, stop initialization and request it
            if (hasPermissions != permissions.length) {
                ActivityCompat.requestPermissions(context, permissions,
                        Constants.SplashActivity.REQUEST_PERMISSIONS);
                this.cancel(true);
            } else {
                //2. Get saved schedule
                String scheduleString = settings.getString(Constants.SharedPreferences.SCHEDULE,
                        Constants.LocationService.DEFAULT_SCHEDULE);
                Schedule schedule = gson.fromJson(scheduleString, Schedule.class);
                //3. If schedule is defined
                if (schedule != null) {
                    // Start location service using schedule handler
                    ScheduleHandler scheduleHandler = new ScheduleHandler(getApplicationContext(), schedule.getDays());
                    scheduleHandler.schedule(LocationService.class);
                } else {
                    // Start location directly
                    startService(new Intent(getApplicationContext(), LocationService.class));
                }
                //4. Start evacuation service
                startService(new Intent(getApplicationContext(), EvacuationService.class));
            }
        }

        /**
         * Wait loading, update progress in each iteration
         *
         * @param params void param
         * @return void return
         */
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


        /**
         * Update progress bar interface with loading value
         *
         * @param values progress updated
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            int i = values[0];
            progress.setProgress(i);
        }

        /**
         * After load the service and verify the permissions, start MainActivity and finish it
         *
         * @param param void param
         */
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
