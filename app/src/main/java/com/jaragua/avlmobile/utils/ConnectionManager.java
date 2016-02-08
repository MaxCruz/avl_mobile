package com.jaragua.avlmobile.utils;

import android.content.Context;
import android.util.Log;

import com.jaragua.avlmobile.persistences.DataSource;
import com.jaragua.avlmobile.persistences.EvacuationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectionManager {

    private static final String TAG = ConnectionManager.class.getSimpleName();
    private OkHttpClient client;
    private DeviceProperties deviceProperties;
    private DataSource dataSource;

    public ConnectionManager(Context context) {
        client = new OkHttpClient();
        deviceProperties = new DeviceProperties(context);
        dataSource = DataSource.getInstance(context);
    }

    public void sendEventToDriverHttp(final String url, final String imei, final String product, final String entities) {
        new Thread() {

            @Override
            public void run() {
                String entityPackage = buildRequestToDriverHttp(imei, product, entities);
                if (deviceProperties.checkConnectivity()) {
                    String httpResponse = post(url, entityPackage);
                    if (httpResponse.contains(Constants.ConnectionManager.RESPONSE_DRIVER)) {
                        Log.d(TAG, "SENT TO DRIVER: " + entityPackage);
                    } else {
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

    public String buildRequestToDriverHttp(String imei, String product, String entities) {
        JSONObject jsonObjectToSend = new JSONObject();
        try {
            jsonObjectToSend.put(Constants.ConnectionManager.IMEI_KEY, imei);
            jsonObjectToSend.put(Constants.ConnectionManager.PRODUCT, product);
            jsonObjectToSend.put(Constants.ConnectionManager.ENTITIES_KEY, new JSONArray(entities));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectToSend.toString();
    }

    private void saveDataToEvacuate(String url, String entityPackage) {
        Log.d(TAG, "SAVE IN EVACUATION: " + entityPackage);
        EvacuationModel model = new EvacuationModel(url, entityPackage, 0, 1);
        dataSource.create(model);
    }

    private String post(String url, String json) {
        try {
            RequestBody body = RequestBody.create(Constants.ConnectionManager.JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch(IOException ex) {
            return "";
        }
    }

}