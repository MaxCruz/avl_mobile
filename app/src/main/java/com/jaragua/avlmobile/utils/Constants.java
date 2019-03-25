package com.jaragua.avlmobile.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.MediaType;

public class Constants {

    public static final String SERVER_URL = "http://50.116.42.121:8081/index.php";
    public static final String PREFERENCES = "AVLMobilePreferences";

    public enum Event {

        READ_TIME(44), READ_DISTANCE(45), PANIC(1);

        private final int value;

        Event(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    public static  class SharedPreferences {
        public static final String TIME_LIMIT = "time_limit";
        public static final String TX_DISTANCE = "tx_distance";
        public static final String SCHEDULE = "schedule";
    }

    public static  class EvacuationService {
        public static final int READ_TIME = 30000;
    }

    public static class LocationService {
        public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        public static final int DISCARD_POSITIONS = 10;
        public static final int DISTANCE_LIMIT = 50;
        public static final float FILTER_ACCURACY = 50F;
        public static final int TIME_LIMIT = 60;
        public static final int TX_DISTANCE = 50;
        public static final int NOTIFICATION_ID = 1784;
        public static final String DEFAULT_SCHEDULE = "";
    }

    public static class ConnectionManager {
        public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        public static final String RESPONSE_DRIVER = "OK";
        public static final String PRODUCT = "avl_mobile";
    }

    public static class DataBaseHelper {
        public static final String DATABASE_NAME = "avlmobile.db";
        public static final int DATABASE_VERSION = 2;
    }

    public static class EvacuationModel {
        public static final String TABLE = "evacuation";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_DATA = "data";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_COUNT = "count";
    }

    public static class MessageModel {
        public static final String TABLE = "message";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_SERVER_ID = "server_id";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_RECEIVED = "received";
        public static final String COLUMN_RESPONSE = "response";
    }

    public static class SplashActivity {
        public static final int REQUEST_PERMISSIONS = 1;
    }

    public static class MainActivity {
        public static final int REFRESH_INTERVAL = 30 * 1000;
    }

    public static class ScheduleBroadcastReceiver {
        public static final String ACTION = "com.jaragua.avlmobile.SCHEDULE";
        public static final String CLASS = "class";
        public static final String OPERATION = "operation";
    }

}
