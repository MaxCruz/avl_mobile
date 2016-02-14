package com.jaragua.avlmobile.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.activities.MainActivity;
import com.jaragua.avlmobile.entities.FrameLocation;
import com.jaragua.avlmobile.utils.ConnectionManager;
import com.jaragua.avlmobile.utils.Constants;
import com.jaragua.avlmobile.utils.DeviceProperties;
import com.jaragua.avlmobile.utils.Geo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationService extends Service implements LocationListener {

    private static final String TAG = LocationService.class.getSimpleName();
    private LocationManager locationManager;
    private long accumulatedDistance;
    private FrameLocation lastTxFrame;
    private LocalBroadcastManager localBroadcastManager;
    private int autoreportTime = Constants.LocationService.TIME_LIMIT;
    private int distance2Tx = Constants.LocationService.TX_DISTANCE;
    private NumberFormat numberFormat;
    private int positionDiscardCounter = Constants.LocationService.DISCARD_POSITIONS;
    private long elapsedAutoreport = 0;
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private DeviceProperties deviceProperties;
    private ConnectionManager connectionManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        connectionManager = new ConnectionManager(this);
        deviceProperties = new DeviceProperties(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(getBaseContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lastTxFrame = new FrameLocation(0, 0, 0, 0, 0, new Date());
        numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setGroupingUsed(false);
        Log.d(TAG, "SERVICE CREATED");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
            Process.killProcess(Process.myPid());
        }
        Log.d(TAG, "SERVICE STARTED [AUTOREPORT:" + autoreportTime + ", DISTANCE: " + distance2Tx + "]");
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
                    float horizontal = location.getAccuracy();
                    float hdop = horizontal / 5;
                    Log.d(TAG, "HDOP: " + hdop);
                    if (hdop > 5) {
                        positionDiscardCounter = Constants.LocationService.DISCARD_POSITIONS;
                        return;
                    }
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
            if (!location.hasAccuracy() || location.getAccuracy() > Constants.LocationService.FILTER_ACCURACY) {
                return;
            }
            if (lastTxFrame.isValid()) {
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
                    lastTxFrame.setBearing(location.getBearing());
                    lastTxFrame.setGpsDate(new Date());
                }
                if (accumulatedDistance >= distance2Tx) {
                    Log.d(TAG, "DISTANCE: " + accumulatedDistance);
                    sendLocationFrame(lastTxFrame, Constants.Event.READ_DISTANCE);
                    elapsedAutoreport = SystemClock.elapsedRealtime();
                } else if (SystemClock.elapsedRealtime() > (elapsedAutoreport + (autoreportTime * 1000))) {
                    lastTxFrame.setGpsDate(new Date());
                    sendLocationFrame(lastTxFrame, Constants.Event.READ_TIME);
                    elapsedAutoreport = SystemClock.elapsedRealtime();
                }
            } else {
                lastTxFrame.setLatitude(location.getLatitude());
                lastTxFrame.setLongitude(location.getLongitude());
                lastTxFrame.setAltitude(location.getAltitude());
                lastTxFrame.setDistance(accumulatedDistance);
                lastTxFrame.setSpeed(location.getSpeed());
                lastTxFrame.setAccuracy(location.getAccuracy());
                lastTxFrame.setBearing(location.getBearing());
                lastTxFrame.setGpsDate(new Date());
                sendLocationFrame(lastTxFrame, Constants.Event.READ_DISTANCE);
            }
        }
    }

    private void sendLocationFrame(FrameLocation location, Constants.Event motive) throws JSONException {
        Log.d(TAG, "MOTIVE: " + motive);
        JSONObject locationEntity = new JSONObject();
        locationEntity.put("motive", motive.getValue());
        locationEntity.put("date", Constants.LocationService.FORMAT_DATE.format(location.getGpsDate()));
        locationEntity.put("latitude", location.getLatitude());
        locationEntity.put("longitude", location.getLongitude());
        locationEntity.put("altitude", Math.round(location.getAltitude()));
        locationEntity.put("speed", location.getSpeed());
        locationEntity.put("course", numberFormat.format(location.getBearing()));
        locationEntity.put("distance", Math.round(location.getDistance()));
        locationEntity.put("accuracy", numberFormat.format(location.getAccuracy()));
        locationEntity.put("imei", deviceProperties.getImei());
        JSONArray entityArray = new JSONArray();
        entityArray.put(locationEntity);
        accumulatedDistance = 0;
        sendBroadcast(motive.name(), location);
        connectionManager.sendEventToDriverHttp(Constants.SERVER_URL, deviceProperties.getImei(),
                Constants.ConnectionManager.PRODUCT, entityArray.toString());
        Log.d(TAG, "TO DRIVER: " + entityArray.toString());
    }

    protected void sendBroadcast(String motive, FrameLocation frame) {
        Intent intent = new Intent(Constants.LocationService.BROADCAST);
        intent.putExtra(Constants.LocationService.MOTIVE, motive);
        intent.putExtra(Constants.LocationService.FRAME, frame);
        Log.d(TAG, "BROADCAST: " + Constants.LocationService.BROADCAST + " DATA: [" + motive +
                " = " + frame.toString() + "]");
        localBroadcastManager.sendBroadcast(intent);
    }

}