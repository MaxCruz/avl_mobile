package com.jaragua.avlmobile.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.jaragua.avlmobile.persistences.DataSource;
import com.jaragua.avlmobile.persistences.EvacuationModel;
import com.jaragua.avlmobile.persistences.ModelInterface;
import com.jaragua.avlmobile.utils.Constants;
import com.jaragua.avlmobile.utils.DeviceProperties;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EvacuationService extends Service {

	private static final String TAG = EvacuationService.class.getSimpleName();
	private boolean running;
	private DataSource dataSource;
	private OkHttpClient client;
	private DeviceProperties deviceProperties;

	private Thread process = new Thread() {

		@Override
		public void run() {
			super.run();
			while (running) {
				if (deviceProperties.checkConnectivity()) {
					List<ModelInterface> evacuationList = dataSource.read(new EvacuationModel(), null, null);
					Log.d(TAG, "EVACUATION SIZE: " + evacuationList.size());
					if (evacuationList.size() > 0) {
						for (ModelInterface data : evacuationList) {
							EvacuationModel dataModel = (EvacuationModel) data;
							String url = dataModel.getUrl();
							String httpResponse = post(url, dataModel.getData());
							if (httpResponse.contains(Constants.ConnectionManager.RESPONSE_DRIVER)) {
								Log.d(TAG, "SENT TO DRIVER: " + dataModel.getData());
								dataSource.delete(dataModel, dataModel.getModelId() + " = ?",
                                        new String[] { String.valueOf(dataModel.getId()) });
							} else {
								Log.d(TAG, "EVACUATION FAILURE");
                                int i = dataModel.getCount() + 1;
                                dataModel.setCount(i);
                                dataSource.update(dataModel, dataModel.getModelId() + " = ?", new String[]{String.valueOf(dataModel.getId())});
							}
							Log.d(TAG, "DRIVER RESPONSE: " + httpResponse);
						}
					}
				}
				try {
					Thread.sleep(Constants.EvacuationService.READ_TIME);
				} catch (InterruptedException e) {
					running = false;
					e.printStackTrace();
				}

			}
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "SERVICE CREATED");
		dataSource = DataSource.getInstance(this);
		client = new OkHttpClient();
		deviceProperties = new DeviceProperties(this);
		running = true;
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "SERVICE STARTED");
		process.start();
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "SERVICE STOPPED");
		process.interrupt();
		running = false;
		super.onDestroy();
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
