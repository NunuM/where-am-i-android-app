package me.nunum.whereami.utils;

import java.util.UUID;

/**
 * Created by bullet on 08-01-2018.
 */

public class AppConfig {

    public static final String PREFERENCES_KEY_NAME = "localizations";

    public static final Integer REMOTE_HOST_SINK_PORT = 8080;
    public static final Integer REMOTE_HOST_EXCHANGE_PORT = 9000;

    public static final Integer POOL_SIZE = 1;
    public static final Integer SINKER_DELAY = 1;
    public static final Integer SINKER_PERIOD = 30;

    public static final Integer PRODUCER_DELAY = 1;
    public static final Integer PRODUCER_PERIOD = 30;

    public static final String HTTP_REMOTE_HOST = "http://192.168.1.6:8080";
    public static final String HTTP_LOCALIZATIONS_RESOURCE = "/localization";
    public static final String HTTP_LOCALIZATION_POSITIONS_RESOURCE = "localization/:id/position";
    public static final String HTTP_PAGE_LOCALIZATION_RESOURCE = "/page/:x";
    public static final String HTTP_TRAINING_RESOURCE = "/localization/:id/train";
    public static final String HTTP_LOCALIZATION_SPAM_RESOURCE = "localization/:id/spam";
    public static final String HTTP_POSITION_SPAM_RESOURCE = "localization/:id/position/:it/spam";
    public static final String HTTP_SAMPLES_RESOURCE = "fingerprint";
    public static final String HTTP_ALGORITHM_RESOURCE = "algorithm";
    public static final String HTTP_DEVICE_RESOURCE = "device";
    public static final Boolean HTTP_PAGINATE_ONLY_MY_LOCALIZATIONS= false;

    public static final String HTTP_PREDICTION_RESOURCE = "localization/:id/predict";


    public static final String HTTP_POSTS_RESOURCE = "/post";

    public static final Boolean PAGINATION_STATE = Boolean.TRUE;

    public static final Integer DATABASE_BATCH = 10;

    public static final String DEFAULT_USERNAME = "Anonymous";

    private static final String LOCALIZATION_URI = HTTP_REMOTE_HOST + HTTP_LOCALIZATIONS_RESOURCE;
    public static final String POSITIONS_URI = LOCALIZATION_URI + "/%d" + HTTP_LOCALIZATION_POSITIONS_RESOURCE;

    public static final String DATABASE_NAME = "wifi";
    public static final Integer DATABASE_VERSION = 1;

    public static final String TCP_PROTOCOL = "tcp";
    public static final String HTTP_PROTOCOL = "http";

    public static final String APPLICATION_INSTANCE = UUID.randomUUID().toString();

    public static final String JSON_MIME_TYPE_HEADER = "application/json";

    public static final Integer DEFAULT_ALGORITHM = 1;

    public static final int FIRST_PAGE = 1;

    public static final String CONNECTIVITY_TEST_HOST = "http://www.mocky.io";

    public static final String BROADCAST_NEW_NOTIFICATION_ACTION = "me_nunum_whereami_new_notification";
}
