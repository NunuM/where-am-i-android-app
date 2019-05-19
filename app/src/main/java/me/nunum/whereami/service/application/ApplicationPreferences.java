package me.nunum.whereami.service.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import me.nunum.whereami.utils.AppConfig;

/**
 * Created by handson on 07/02/2018.
 */

public class ApplicationPreferences {

    private final Context context;
    private final SharedPreferences preferences;

    private static ApplicationPreferences instance = null;

    private static final String TAG = "ApplicationPreferences";

    protected ApplicationPreferences(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static enum KEYS {
        PREDICTION_RESOURCE {
            @Override
            public String defaultValue() {
                return "prediction";
            }

            @Override
            public boolean isString() {
                return true;
            }

            @Override
            public String keyName() {
                return "http_prediction_resource";
            }
        },
        FINGERPRINT_RESOURCE {
            @Override
            public String defaultValue() {
                return "fingerprint";
            }

            @Override
            public boolean isString() {
                return true;
            }

            @Override
            public String keyName() {
                return "http_fingerprint_resource";
            }
        },
        ONLINE_PROTOCOL {
            @Override
            public String defaultValue() {
                return "TCP";
            }

            @Override
            public boolean isString() {
                return true;
            }

            @Override
            public String keyName() {
                return "http_online_protocol";
            }
        },
        OFFLINE_PROTOCOL {
            @Override
            public String defaultValue() {
                return "TCP";
            }

            @Override
            public boolean isString() {
                return true;
            }

            @Override
            public String keyName() {
                return "http_offline_protocol";
            }
        },
        REMOTE_HOST_SINK_PORT {
            @Override
            public String keyName() {
                return "tcp_remote_host_offline_port";
            }

            public boolean isInt() {
                return true;
            }

            @Override
            public Integer defaultValue() {
                return AppConfig.REMOTE_HOST_SINK_PORT;
            }

        },
        REMOTE_HOST_EXCHANGE_PORT {
            @Override
            public String keyName() {
                return "tcp_remote_host_online_port";
            }

            public boolean isInt() {
                return true;
            }

            @Override
            public Integer defaultValue() {
                return AppConfig.REMOTE_HOST_EXCHANGE_PORT;
            }
        },
        POOL_SIZE {
            @Override
            public String keyName() {
                return "pool_size";
            }

            public boolean isInt() {
                return true;
            }

            @Override
            public Integer defaultValue() {
                return AppConfig.POOL_SIZE;
            }
        },
        SINKER_DELAY {
            @Override
            public String keyName() {
                return "sinker_delay";
            }

            public boolean isInt() {
                return true;
            }

            @Override
            public Integer defaultValue() {
                return AppConfig.SINKER_DELAY;
            }
        },
        SINKER_PERIOD {
            @Override
            public String keyName() {
                return "sinker_period";
            }

            public boolean isInt() {
                return true;
            }

            @Override
            public Integer defaultValue() {
                return AppConfig.SINKER_PERIOD;
            }
        },
        PRODUCER_DELAY {
            @Override
            public String keyName() {
                return "producer_delay";
            }

            public boolean isInt() {
                return true;
            }

            @Override
            public Integer defaultValue() {
                return AppConfig.PRODUCER_DELAY;
            }
        },
        PRODUCER_PERIOD {
            @Override
            public String keyName() {
                return "producer_period";
            }

            public boolean isInt() {
                return true;
            }

            @Override
            public Integer defaultValue() {
                return AppConfig.PRODUCER_PERIOD;
            }
        },
        HTTP_REMOTE_HOST {
            @Override
            public String keyName() {
                return "http_remote_host";
            }

            public boolean isString() {
                return true;
            }

            @Override
            public String defaultValue() {
                return AppConfig.HTTP_REMOTE_HOST;
            }
        },
        HTTP_LOCALIZATIONS_RESOURCE {
            @Override
            public String keyName() {
                return "http_localization_resource";
            }

            public boolean isString() {
                return true;
            }

            @Override
            public String defaultValue() {
                return AppConfig.HTTP_LOCALIZATIONS_RESOURCE;
            }
        },
        HTTP_LOCALIZATION_POSITIONS_RESOURCE {
            @Override
            public String keyName() {
                return "http_position_resource";
            }

            public boolean isString() {
                return true;
            }

            public String defaultValue() {
                return AppConfig.HTTP_LOCALIZATION_POSITIONS_RESOURCE;
            }

        },
        PAGINATION_STATE {
            @Override
            public Boolean defaultValue() {
                return AppConfig.PAGINATION_STATE;
            }

            @Override
            public String keyName() {
                return "pagination_state";
            }

            public boolean isBoolean() {
                return true;
            }
        },
        HTTP_PAGE_LOCALIZATION_RESOURCE {
            @Override
            public String defaultValue() {
                return AppConfig.HTTP_PAGE_LOCALIZATION_RESOURCE;
            }

            @Override
            public String keyName() {
                return "http_page_localization_resource";
            }

            public boolean isString() {
                return true;
            }
        },
        HTTP_POSTS_RESOURCE {
            @Override
            public String defaultValue() {
                return AppConfig.HTTP_POSTS_RESOURCE;
            }

            @Override
            public String keyName() {
                return "http_posts_resource";
            }

            public boolean isString() {
                return true;
            }
        },
        DEFAULT_USERNAME {
            @Override
            public String defaultValue() {
                return AppConfig.DEFAULT_USERNAME;
            }

            @Override
            public String keyName() {
                return "default_username";
            }

            @Override
            public boolean isString() {
                return true;
            }
        },
        HTTP_TRAINING_RESOURCE {
            @Override
            public String defaultValue() {
                return AppConfig.HTTP_TRAINING_RESOURCE;
            }

            @Override
            public String keyName() {
                return "http_train_resource";
            }

            @Override
            public boolean isString() {
                return true;
            }
        },
        HTTP_LOCALIZATION_SPAM_RESOURCE {
            @Override
            public String defaultValue() {
                return AppConfig.HTTP_LOCALIZATION_SPAM_RESOURCE;
            }

            @Override
            public String keyName() {
                return "http_localization_spam_resource";
            }

            @Override
            public boolean isString() {
                return true;
            }
        },
        HTTP_POSITION_SPAM_RESOURCE {
            public String defaultValue() {
                return AppConfig.HTTP_POSITION_SPAM_RESOURCE;
            }

            @Override
            public String keyName() {
                return "http_position_spam_resource";
            }

            @Override
            public boolean isString() {
                return true;
            }
        },
        HTTP_SAMPLES_RESOURCE {
            @Override
            public String defaultValue() {
                return AppConfig.HTTP_SAMPLES_RESOURCE;
            }

            @Override
            public String keyName() {
                return "http_samples_resource";
            }

            @Override
            public boolean isString() {
                return true;
            }
        },
        HTTP_ALGORITHM_RESOURCE {
            @Override
            public String defaultValue() {
                return AppConfig.HTTP_ALGORITHM_RESOURCE;
            }

            @Override
            public String keyName() {
                return "http_algorithm_resource";
            }

            @Override
            public boolean isString() {
                return true;
            }
        },
        INSTANCE_ID {
            @Override
            public String defaultValue() {
                return AppConfig.APPLICATION_INSTANCE;
            }

            @Override
            public String keyName() {
                return "application_instance_id";
            }

            @Override
            public boolean isString() {
                return true;
            }
        },
        DEFAULT_ALGORITHM {
            @Override
            public Integer defaultValue() {
                return AppConfig.DEFAULT_ALGORITHM;
            }

            @Override
            public String keyName() {
                return "application_instance_id";
            }

            @Override
            public boolean isInt() {
                return true;
            }
        },
        ACCESS_WIFI_PERMISSION {
            @Override
            public Boolean defaultValue() {
                return false;
            }

            @Override
            public String keyName() {
                return "application_access_wifi_permission";
            }

            @Override
            public boolean isBoolean() {
                return true;
            }
        },
        CHANGE_WIFI_PERMISSION {
            @Override
            public Boolean defaultValue() {
                return false;
            }

            @Override
            public String keyName() {
                return "application_change_wifi_permission";
            }

            @Override
            public boolean isBoolean() {
                return true;
            }
        },
        COARSE_LOCALIZATION {
            @Override
            public Boolean defaultValue() {
                return false;
            }

            @Override
            public String keyName() {
                return "application_coarse_localization_permission";
            }

            @Override
            public boolean isBoolean() {
                return true;
            }
        }, USERNAME {
            @Override
            public String defaultValue() {
                return "";
            }

            @Override
            public String keyName() {
                return "username";
            }

            @Override
            public boolean isString() {
                return true;
            }
        };

        public boolean isString() {
            return false;
        }

        public boolean isInt() {
            return false;
        }

        public boolean isBoolean() {
            return false;
        }

        public abstract <T> T defaultValue();

        public abstract String keyName();
    }


    @NonNull
    public final Integer getIntegerKey(KEYS keys) {
        if (!keys.isInt()) {
            throw new IllegalArgumentException("Key must be of integer type");
        }

        try {
            final String string = this.preferences.getString(keys.keyName(), keys.defaultValue().toString());
            return Integer.valueOf(string);
        } catch (ClassCastException | NumberFormatException e) {
            Log.i(TAG, "getIntegerKey: cannot get pref " + keys.keyName() + " value due cast exception", e);
        }

        return keys.defaultValue();
    }

    @NonNull
    public final Boolean getBooleanKey(KEYS keys) {
        if (!keys.isBoolean()) {
            throw new IllegalArgumentException("Key must be of boolean type");
        }

        try {
            final Boolean value = this.preferences.getBoolean(keys.keyName(), (Boolean) keys.defaultValue());
            return value;
        } catch (ClassCastException | NumberFormatException e) {
            Log.i(TAG, "getBooleanKey: cannot get pref " + keys.keyName() + " value due cast exception", e);
        }

        return keys.defaultValue();
    }


    public final String getStringKey(KEYS keys) {

        if (!keys.isString()) {
            throw new IllegalArgumentException("Key must be of string type");
        }
        final String o = keys.defaultValue();
        return this.preferences.getString(keys.keyName(), o);
    }


    public boolean setStringKey(KEYS key, final String value) {
        if (!key.isString()) {
            throw new IllegalArgumentException("Key must be of string type");
        }

        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putString(key.keyName(), value);
        edit.apply();
        return true;
    }

    public boolean setIntegerKey(KEYS key, final int value) {
        if (!key.isString()) {
            throw new IllegalArgumentException("Key must be of string type");
        }


        final SharedPreferences preferences = this.context.getSharedPreferences(AppConfig.PREFERENCES_KEY_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(key.keyName(), value);
        edit.apply();
        return true;
    }

    public boolean setBooleanKey(KEYS key, final boolean value) {
        if (!key.isBoolean()) {
            throw new IllegalArgumentException("Key must be of string type");
        }

        final SharedPreferences preferences = this.context.getSharedPreferences(AppConfig.PREFERENCES_KEY_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key.keyName(), value);
        edit.apply();

        return true;
    }


    public void persistIfNull(KEYS key) {

        String string = this.preferences.getString(key.keyName(), null);

        if (string == null) {
            SharedPreferences.Editor editor = this.preferences.edit();
            editor.putString(key.keyName(), (String) key.defaultValue());
            editor.apply();
        }

    }

    public void persistIfNull(KEYS key, String value) {

        String string = this.preferences.getString(key.keyName(), null);

        if (string == null) {
            SharedPreferences.Editor editor = this.preferences.edit();
            editor.putString(key.keyName(), value);
            editor.apply();
        }

    }

    public static ApplicationPreferences instance(final Context context) {

        if (instance == null) {
            instance = new ApplicationPreferences(context);
        }
        return instance;
    }

}