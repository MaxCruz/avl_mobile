package com.jaragua.avlmobile.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.activities.MainActivity;
import com.jaragua.avlmobile.entities.FrameLocation;
import com.jaragua.avlmobile.utils.ConnectionManager;
import com.jaragua.avlmobile.utils.Constants;
import com.jaragua.avlmobile.utils.DeviceProperties;
import com.jaragua.avlmobile.utils.Geo;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationService extends Service implements LocationListener {

    private static final String TAG = LocationService.class.getSimpleName();
    private LocationManager locationManager;
    private long accumulatedDistance;
    private FrameLocation lastTxFrame;
    private int timeLimit;
    private int txDistance;
    private int positionDiscardCounter = Constants.LocationService.DISCARD_POSITIONS;
    private long elapsedAutoreport = 0;
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private ConnectionManager connectionManager;
    private SharedPreferences settings;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        settings = getSharedPreferences(Constants.PREFERENCES, 0);
        readParameters();
        connectionManager = new ConnectionManager(this);
        DeviceProperties deviceProperties = new DeviceProperties(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lastTxFrame = new FrameLocation();
        lastTxFrame.setImei(deviceProperties.getImei());
        Log.d(TAG, "SERVICE CREATED");
        try {
            Criteria criteria = new Criteria();
            criteria.setCostAllowed(true);
            criteria.setAltitudeRequired(true);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            locationManager.requestLocationUpdates(1000, 0, criteria, this, null);
        } catch (SecurityException ex) {
            ex.printStackTrace();
            Process.killProcess(Process.myPid());
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SERVICE STARTED [AUTOREPORT:" + timeLimit + ", DISTANCE: " + txDistance + "]");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_action_track_changes)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_description))
                .setContentIntent(pendingIntent).build();
        startForeground(Constants.LocationService.NOTIFICATION_ID, notification);
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        accumulatedDistance = 0;
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
        stopForeground(true);
        Log.d(TAG, "SERVICE DESTROYED");
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(final Location location) {
        threadPool.execute(new Runnable() {

            @Override
            public void run() {
                if (positionDiscardCounter <= 0) {
                    try {
                        processLocation(location);
                        Log.d(TAG, "NEW LOCATION: " + location.toString());
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    positionDiscardCounter--;
                }
            }

        });
    }

    private boolean filterHdop(Location location) {
        float horizontal = location.getAccuracy();
        float hdop = horizontal / 5;
        Log.d(TAG, "HDOP: " + hdop);
        return (hdop > 5);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO: Handle when the GPS is disabled
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO: Handle when the GPS is enabled
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private void processLocation(Location location) throws JSONException {
        if (location != null) {
            Log.d(TAG, "ACCURACY: " + location.getAccuracy());
            if (SystemClock.elapsedRealtime() > (elapsedAutoreport + (timeLimit * 1000))) {
                if (!lastTxFrame.isValid()) {
                    lastTxFrame.setLatitude(location.getLatitude());
                    lastTxFrame.setLongitude(location.getLongitude());
                    lastTxFrame.setAltitude(location.getAltitude());
                    lastTxFrame.setDistance(accumulatedDistance);
                    lastTxFrame.setSpeed(location.getSpeed());
                    lastTxFrame.setAccuracy(location.getAccuracy());
                    lastTxFrame.setCourse(location.getBearing());
                    lastTxFrame.setProvider("time");
                }
                lastTxFrame.setDate(Constants.LocationService.FORMAT_DATE.format(new Date()));
                lastTxFrame.setMotive(Constants.Event.READ_TIME.getValue());
                sendLocationFrame(lastTxFrame);
                elapsedAutoreport = SystemClock.elapsedRealtime();
                return;
            }
            if ((!location.hasAccuracy() || location.getAccuracy() >
                    Constants.LocationService.FILTER_ACCURACY) && filterHdop(location)) {
                return;
            }
            if (lastTxFrame.isValid()) {
                lastTxFrame.setProvider(location.getProvider());
                long distanceBetween = Geo.calculateDistance(lastTxFrame.getLatitude(),
                        lastTxFrame.getLongitude(), location.getLatitude(), location.getLongitude());
                if (distanceBetween > Constants.LocationService.DISTANCE_LIMIT) {
                    accumulatedDistance += distanceBetween;
                    lastTxFrame.setLatitude(location.getLatitude());
                    lastTxFrame.setLongitude(location.getLongitude());
                    lastTxFrame.setAltitude(location.getAltitude());
                    lastTxFrame.setDistance(accumulatedDistance);
                    lastTxFrame.setSpeed(location.getSpeed());
                    lastTxFrame.setAccuracy(location.getAccuracy());
                    lastTxFrame.setCourse(location.getBearing());
                    lastTxFrame.setDate(Constants.LocationService.FORMAT_DATE.format(new Date()));
                }
                if (accumulatedDistance >= txDistance) {
                    Log.d(TAG, "DISTANCE: " + accumulatedDistance);
                    lastTxFrame.setMotive(Constants.Event.READ_DISTANCE.getValue());
                    sendLocationFrame(lastTxFrame);
                    elapsedAutoreport = SystemClock.elapsedRealtime();
                }
            } else {
                lastTxFrame.setLatitude(location.getLatitude());
                lastTxFrame.setLongitude(location.getLongitude());
                lastTxFrame.setAltitude(location.getAltitude());
                lastTxFrame.setDistance(accumulatedDistance);
                lastTxFrame.setSpeed(location.getSpeed());
                lastTxFrame.setAccuracy(location.getAccuracy());
                lastTxFrame.setCourse(location.getBearing());
                lastTxFrame.setDate(Constants.LocationService.FORMAT_DATE.format(new Date()));
                lastTxFrame.setMotive(Constants.Event.READ_DISTANCE.getValue());
                sendLocationFrame(lastTxFrame);
            }
        }
    }

    private void readParameters() {
        timeLimit = settings.getInt(Constants.SharedPreferences.TIME_LIMIT,
                Constants.LocationService.TIME_LIMIT);
        txDistance = settings.getInt(Constants.SharedPreferences.TX_DISTANCE,
                Constants.LocationService.TX_DISTANCE);
    }

    private void sendLocationFrame(FrameLocation location) throws JSONException {
        Log.d(TAG, "MOTIVE: " + location.getMotive());
        ArrayList<FrameLocation> locations = new ArrayList<>();
        locations.add(location);
        accumulatedDistance = 0;
        connectionManager.sendEventToDriverHttp(Constants.SERVER_URL, location.getImei(),
                Constants.ConnectionManager.PRODUCT, locations);
        readParameters();
    }

}