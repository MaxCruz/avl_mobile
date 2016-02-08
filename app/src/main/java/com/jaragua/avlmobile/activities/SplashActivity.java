package com.jaragua.avlmobile.activities;

import android.Manifest;
import android.content.Intent;
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

import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.services.EvacuationService;
import com.jaragua.avlmobile.services.LocationService;
import com.jaragua.avlmobile.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView version = (TextView) findViewById(R.id.version);
        progress = (ProgressBar) findViewById(R.id.progress);
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
                if (result.length == 2 &&
                        result[0] == PackageManager.PERMISSION_GRANTED &&
                        result[1] == PackageManager.PERMISSION_GRANTED) {
                    new InitializeTask().execute();
                } else {
                    Toast.makeText(SplashActivity.this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    public class InitializeTask extends AsyncTask<Void, Integer, Void> {

        private int i = 0;
        private String[] permissions = new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE };

        @Override
        protected void onPreExecute() {
            if (ContextCompat.checkSelfPermission(SplashActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashActivity.this, permissions,
                        Constants.SplashActivity.REQUEST_PERMISSIONS);
                this.cancel(true);
            } else {
                startService(new Intent(getApplicationContext(), LocationService.class));
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
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }

    }

}
