package com.jaragua.avlmobile.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.MediaType;

public class Constants {

    public static final String SERVER_URL = "http://50.116.42.121:8081/index.php";

    public enum Event {

        READ_TIME(44),
        READ_DISTANCE(45);

        private final int value;

        Event(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    public static  class EvacuationService {
        public static final int READ_TIME = 30000;
    }

    public static class LocationService {
        public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        public static final String BROADCAST = "com.jaragua.services.LocationService";
        public static final String MOTIVE = "MOTIVE";
        public static final String FRAME = "FRAME";
        public static final int DISCARD_POSITIONS = 10;
        public static final int DISTANCE_LIMIT = 50;
        public static final float FILTER_ACCURACY = 50F;
        public static final int TIME_LIMIT = 300;
        public static final int TX_DISTANCE = 50;
        public static final int NOTIFICATION_ID = 1784;
    }

    public static class ConnectionManager {
        public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        public static final String RESPONSE_DRIVER = "OK";
        public static final String PRODUCT = "avl_mobile";
        public static final String IMEI_KEY = "imei";
        public static final String ENTITIES_KEY = "entities";
    }

    public static class DataBaseHelper {
        public static final String DATABASE_NAME = "avlmobile.db";
        public static final int DATABASE_VERSION = 1;
    }

    public static class EvacuationModel {
        public static final String TABLE = "evacuation";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_DATA = "data";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_COUNT = "count";
    }

    public static class SplashActivity {
        public static final int REQUEST_PERMISSIONS = 1;
    }

    public static class MainActivity {
        public static final String MOTIVE = "motive";
        public static final String DATE = "date";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String ALTITUDE = "altitude";
        public static final String SPEED = "speed";
        public static final String COURSE = "course";
        public static final String DISTANCE = "distance";
        public static final String ACCURACY = "accuracy";
    }

}
