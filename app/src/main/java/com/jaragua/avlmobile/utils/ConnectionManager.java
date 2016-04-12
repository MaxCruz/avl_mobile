package com.jaragua.avlmobile.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.activities.MainActivity;
import com.jaragua.avlmobile.entities.Configuration;
import com.jaragua.avlmobile.entities.DriverRequest;
import com.jaragua.avlmobile.entities.DriverResponse;
import com.jaragua.avlmobile.entities.Message;
import com.jaragua.avlmobile.persistences.DataSource;
import com.jaragua.avlmobile.persistences.EvacuationModel;
import com.jaragua.avlmobile.persistences.MessageModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectionManager {

    private static final String TAG = ConnectionManager.class.getSimpleName();
    private OkHttpClient client;
    private DeviceProperties deviceProperties;
    private DataSource dataSource;
    private Gson gson;
    private SharedPreferences settings;
    private Context context;

    public ConnectionManager(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
        this.deviceProperties = new DeviceProperties(context);
        this.dataSource = DataSource.getInstance(context);
        this.gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
        this.settings = context.getSharedPreferences(Constants.PREFERENCES, 0);
    }

    public void sendEventToDriverHttp(final String url, final String imei,
                                      final String product, final ArrayList<?> entities) {
        new Thread() {

            @Override
            public void run() {
                DriverRequest request = new DriverRequest(imei, product, entities);
                String entityPackage = gson.toJson(request);
                if (deviceProperties.checkConnectivity()) {
                    Log.d(TAG, "SEND TO DRIVER: " + entityPackage);
                    String httpResponse = post(url, entityPackage);
                    if (!processResponseFromDriver(httpResponse)) {
                        Log.d(TAG, "FAILURE DRIVER RESPONSE");
                        saveDataToEvacuate(url, entityPackage);
                    }
                    Log.d(TAG, "DRIVER RESPONSE: " + httpResponse);
                } else {
                    Log.d(TAG, "NOT CONNECTED");
                    saveDataToEvacuate(url, entityPackage);
                }
            }

        }.start();
    }

    private void saveDataToEvacuate(String url, String entityPackage) {
        Log.d(TAG, "SAVE IN EVACUATION: " + entityPackage);
        EvacuationModel model = new EvacuationModel(url, entityPackage, 0, 1);
        dataSource.create(model);
    }

    public boolean processResponseFromDriver(String responseJson) {
        boolean result = false;
        try {
            Log.d(TAG, "RESPONSE FROM DRIVER:" + responseJson);
            DriverResponse response = gson.fromJson(responseJson, DriverResponse.class);
            if (response.getStatus().equals(Constants.ConnectionManager.RESPONSE_DRIVER)) {
                result = true;
                if (response.getConfiguration() != null) {
                    Configuration configuration = response.getConfiguration();
                    int timeLimit = settings.getInt(Constants.SharedPreferences.TIME_LIMIT,
                            Constants.LocationService.TIME_LIMIT);
                    int txDistance = settings.getInt(Constants.SharedPreferences.TX_DISTANCE,
                            Constants.LocationService.TX_DISTANCE);
                    SharedPreferences.Editor editor = settings.edit();
                    if (timeLimit != configuration.getTimeLimit()) {
                        editor.putInt(Constants.SharedPreferences.TIME_LIMIT,
                                configuration.getTimeLimit());
                    }
                    if (txDistance != configuration.getTxDistance()) {
                        editor.putInt(Constants.SharedPreferences.TX_DISTANCE,
                                configuration.getTxDistance());
                    }
                    editor.apply();
                }
                if (response.getMessages() != null) {
                    List<Message> messages = response.getMessages();
                    for (Message message : messages) {
                        String received = Constants.LocationService.FORMAT_DATE.format(new Date());
                        MessageModel model = new MessageModel();
                        model.setServerId(message.getId());
                        model.setStatus(0);

                        String temp = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
                        message.setMessage(temp);

                        model.setMessage(message.getMessage());
                        model.setReceived(received);
                        model.setResponse(message.getResponse());
                        dataSource.create(model);
                        notificateNewMessage(message.getMessage());
                    }
                }
            }
        } catch (JsonParseException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public String post(String url, String json) {
        try {
            RequestBody body = RequestBody.create(Constants.ConnectionManager.JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException ex) {
            return "";
        }
    }

    private void notificateNewMessage(String message) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_markunread_black_36dp)
                .setContentTitle(context.getString(R.string.new_message))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent).build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

}