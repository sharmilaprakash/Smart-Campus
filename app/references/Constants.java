package references;

/**
 * Created by sank on 4/11/16.
 */
public class Constants {
    static public class KeyWords {
        public static final String DEVICE_ID = "deviceId";
        public static final String BEACON_ID = "beaconId";
        public static final int TOTAL_NUMBER_OF_BEACONS = 10;
        public static final String ML_EVENT_IDS = "";
        public static final String LOG_SEPERATOR = "ML_LOG : ";
    }

    static public class Urls {
        public static final String ML_URL_POST = "http://localhost:7070";
        public static final String ML_URL_GET = "https://0.0.0.0:8000";
        public static final String SOLR_URL = "http://192.168.0.19:8983/solr";
    }

    static public class queries {
        public static final String query = "*";
    }

    static public class MlConstants {
        public static final String SET = "$set";
        public static final String EVENT = "item";
        public static final String USER = "user";
        public static final String STAR = "star";
        //        public static final String VIEW = "view";
        public static final String VIEW = "buy";

//                public static final String ACCESS_KEY = "tkErSeMniRjidCzxYTuDSGCBWuovWI3lXmjRQGpvH2QBSC0cLRQaERzyYOEMbMtj";
        public static final String ACCESS_KEY = "tYfJlO3dsaV5wf86mZbD8rdHB0LFE1n-a2bl6oOq2pFIUIYn-wngn04Ip1FkwiY5";
    }


}
