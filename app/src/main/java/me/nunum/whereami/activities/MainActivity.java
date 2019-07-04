package me.nunum.whereami.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.ClientError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.HashMap;

import me.nunum.whereami.R;
import me.nunum.whereami.fragments.HomeFragment;
import me.nunum.whereami.fragments.LocalizationFragment;
import me.nunum.whereami.fragments.NewLocalizationFragment;
import me.nunum.whereami.fragments.NewPositionFragment;
import me.nunum.whereami.fragments.NewTrainingRequestFragment;
import me.nunum.whereami.fragments.PositionDetailsFragment;
import me.nunum.whereami.fragments.PositionFragment;
import me.nunum.whereami.fragments.PredictionDashboardFragment;
import me.nunum.whereami.fragments.PredictionFragment;
import me.nunum.whereami.fragments.TrainingStatusFragment;
import me.nunum.whereami.framework.Cache;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.framework.OnSample;
import me.nunum.whereami.framework.StreamFlow;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.TrainingProgress;
import me.nunum.whereami.model.request.NewLocalizationRequest;
import me.nunum.whereami.model.request.NewPositionRequest;
import me.nunum.whereami.model.request.NewTrainingRequest;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;
import me.nunum.whereami.service.StreamFlowService;
import me.nunum.whereami.service.SyncOfflineDataService;
import me.nunum.whereami.service.application.ApplicationPreferences;
import me.nunum.whereami.service.database.DatabaseStats;
import me.nunum.whereami.service.notification.FireNotificationManager;
import me.nunum.whereami.utils.PermissionRequestCodes;

public class MainActivity
        extends
        AppCompatActivity
        implements
        LocalizationFragment.OnListFragmentInteractionListener,
        PositionFragment.OnListFragmentInteractionListener,
        PositionDetailsFragment.OnFragmentInteractionListener,
        NewPositionFragment.OnFragmentInteractionListener,
        NewLocalizationFragment.OnFragmentInteractionListener,
        TrainingStatusFragment.OnFragmentInteractionListener,
        NewTrainingRequestFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        PredictionFragment.OnListFragmentInteractionListener,
        PredictionDashboardFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final Gson gson = new Gson();
    private Position position = null;
    private Cache servicesCache = null;
    private Localization localization = null;
    private StreamFlowService streamFlow = null;
    private ApplicationPreferences preferences;

    private PositionDetailsFragment positionDetailsFragment;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            clearBackStack();
            changeActionBarBackButtonState(false);

            switch (item.getItemId()) {
                case R.id.navigation_home:

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.am_container, HomeFragment.newInstance())
                            .commitAllowingStateLoss();

                    return true;
                case R.id.navigation_localization:

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.am_container, LocalizationFragment.newInstance())
                            .commitAllowingStateLoss();

                    return true;
                case R.id.navigation_prediction:

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.am_container, PredictionFragment.newInstance())
                            .commitAllowingStateLoss();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        preferences = ApplicationPreferences.instance(this.getApplicationContext());

        streamFlow = new StreamFlowService(this.getApplicationContext());

        servicesCache = new Cache(this.getApplicationContext());

        positionDetailsFragment = PositionDetailsFragment.newInstance();

        preferences.persistIfNull(ApplicationPreferences.KEYS.INSTANCE_ID);

        FirebaseInstanceId
                .getInstance()
                .getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {

                        HttpService service = (HttpService) servicesCache.get(Services.HTTP);

                        service.updateDevice(new Device(instanceIdResult.getToken()), new OnResponse<Device>() {
                            @Override
                            public void onSuccess(Device o) {
                                Log.i(TAG, "onSuccess: Send FireBase token at application start");
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.e(TAG, "onFailure: Could not update FireBase token at application start", throwable);
                            }
                        });
                    }
                });


        FirebaseMessaging
                .getInstance()
                .subscribeToTopic(FireNotificationManager.NEW_POSTS_TOPIC);

        FirebaseMessaging
                .getInstance()
                .subscribeToTopic(FireNotificationManager.NEW_ALGORITHM_TOPIC);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.am_container, HomeFragment.newInstance())
                .commitAllowingStateLoss();


        final Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.containsKey("action")) {
                    HashMap<String, String> map = new HashMap<>();

                    for (String key : extras.keySet()) {
                        Object o = extras.get(key);
                        if (o != null)
                            map.put(key, o.toString());
                    }

                    FireNotificationManager.processNotificationData(getApplicationContext(), map, System.currentTimeMillis());

                    startActivity(new Intent(this, NotificationActivity.class));

                    extras.clear();
                }
            }
        }


        startService(new Intent(this, SyncOfflineDataService.class));
    }

    public void onBackPressed() {
        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            changeActionBarBackButtonState(false);
        }
    }


    private void changeActionBarBackButtonState(boolean visibility) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(visibility);

    }


    private void clearBackStack() {
        final FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStackImmediate(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onLocalizationSelected(Localization item) {
        this.localization = item;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.am_container, PositionFragment.newInstance())
                .addToBackStack(PositionFragment.class.getSimpleName())
                .commitAllowingStateLoss();

        changeActionBarBackButtonState(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        final FragmentManager manager = getSupportFragmentManager();

        int counter = manager.getBackStackEntryCount();

        switch (counter) {
            case 0:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;
            case 1:
                manager.popBackStack();
                if (getSupportActionBar() != null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;
            default:
                manager.popBackStack();
                break;
        }

        return true;
    }

    @Override
    public void onPositionSelected(Position item) {

        this.position = item;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.am_container, positionDetailsFragment)
                .addToBackStack(PositionDetailsFragment.class.getSimpleName())
                .commitAllowingStateLoss();
    }

    @Override
    public Localization associatedLocalization() {
        return this.localization;
    }

    @Override
    public Position associatedPosition() {
        return this.position;
    }

    @Override
    public Context context() {
        return this.getApplicationContext();
    }

    @Override
    public void launchBrowserIntent(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void createLocalization(String localization,
                                   final String username,
                                   boolean isPrivate,
                                   double lat,
                                   double lng) {

        HttpService service = (HttpService) this.servicesCache.get(Services.HTTP);

        final NewLocalizationRequest newLocalizationRequest = new NewLocalizationRequest(localization, isPrivate, username, lat, lng);

        service.newLocalization(newLocalizationRequest, new OnResponse<Localization>() {
            @Override
            public void onSuccess(Localization o) {

                preferences.persistIfNull(ApplicationPreferences.KEYS.USERNAME, username);

                getSupportFragmentManager().popBackStack();
                changeActionBarBackButtonState(false);
            }

            @Override
            public void onFailure(Throwable throwable) {

                Log.e(TAG, "onFailure: Not possible to create new localization", throwable);

                if (throwable instanceof ClientError) {
                    ClientError error = (ClientError) throwable;
                    if (error.networkResponse.statusCode == 409) {
                        Toast.makeText(context(), R.string.fnl_new_localization_request_duplicate_failure, Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                Toast.makeText(context(), R.string.fnl_new_localization_request_failure, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean startSampling(OnSample onSampleCallback) {
        if (this.requestWifiPermissions()) {
            return false;
        }

        setScreenAlwaysOnFlag();

        return this.streamFlow.start(this.localization, this.position, onSampleCallback, this.servicesCache);
    }

    @Override
    public boolean stopSampling() {
        if (streamFlow.currentState() == StreamFlow.STREAM_STATE.RUNNING) {
            clearScreenAlwaysOnFlag();
            return streamFlow.stop();
        }

        return true;
    }

    @Override
    public Long numberOfOfflineSamples() {
        final DatabaseStats stats = new DatabaseStats(this.getApplicationContext());
        return stats.totalRecords(associatedPosition());
    }

    @Override
    public Object getService(Services service) {
        return this.servicesCache.get(service);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_settings_options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_options_menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.app_options_menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.app_options_menu_notification:
                startActivity(new Intent(this, NotificationActivity.class));
                return true;
            case R.id.app_options_menu_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setActionBarTitle(String title) {

        final ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void openPredictionDashboardFor(Localization localization) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.am_container, PredictionDashboardFragment.newInstance(localization))
                .addToBackStack(PredictionDashboardFragment.class.getSimpleName())
                .commitAllowingStateLoss();

        changeActionBarBackButtonState(true);

    }

    @Override
    public void openNewTrainingFragment() {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.am_container, NewTrainingRequestFragment.newInstance())
                .addToBackStack(NewTrainingRequestFragment.class.getSimpleName())
                .commitAllowingStateLoss();

    }

    @Override
    public void openNewLocalizationFragment() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestWifiPermissions();

            return;
        }

        double lat = 0;
        double lng = 0;

        if (locationManager != null) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lat = location.getLatitude();
            lng = location.getLongitude();
        }

        final String username = preferences.getStringKey(ApplicationPreferences.KEYS.USERNAME);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.am_container, NewLocalizationFragment.newInstance(lat, lng, username))
                .addToBackStack(NewLocalizationFragment.class.getSimpleName())
                .commitAllowingStateLoss();

        changeActionBarBackButtonState(true);
    }

    @Override
    public void openTrainingStatus(Localization mItem) {

        this.localization = mItem;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.am_container, TrainingStatusFragment.newInstance())
                .addToBackStack(TrainingStatusFragment.class.getSimpleName())
                .commitAllowingStateLoss();

        changeActionBarBackButtonState(true);
    }

    @Override
    public void openNewPositionFragment() {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.am_container, NewPositionFragment.newInstance())
                .addToBackStack(NewPositionFragment.class.getSimpleName())
                .commitAllowingStateLoss();

    }

    @Override
    public void submitNewTanningRequest(Long algorithmId, Long providerId) {


        HttpService service = (HttpService) getService(Services.HTTP);

        service.newTrain(
                associatedLocalization().id(),
                new NewTrainingRequest(algorithmId, providerId),

                new OnResponse<TrainingProgress>() {
                    @Override
                    public void onSuccess(TrainingProgress o) {
                        getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (throwable instanceof ClientError) {
                            int statusCode = ((ClientError) throwable).networkResponse.statusCode;

                            Toast.makeText(getApplicationContext(), getString(R.string.fntr_new_training_algorithm_request_failure_w_status, statusCode), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), R.string.fntr_new_training_algorithm_request_failure, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (this.localization != null) {
            outState.putString(Localization.class.getSimpleName(), gson.toJson(this.localization));
        }

        if (this.position != null) {
            outState.putString(Position.class.getSimpleName(), gson.toJson(this.position));
        }
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        final String localizationKey = Localization.class.getSimpleName();
        final String positionKey = Position.class.getSimpleName();

        if (savedInstanceState.containsKey(localizationKey)) {
            this.localization = gson.fromJson(savedInstanceState.getString(localizationKey), Localization.class);
        }

        if (savedInstanceState.containsKey(positionKey)) {
            this.position = gson.fromJson(savedInstanceState.getString(positionKey), Position.class);
        }
    }

    private boolean requestWifiPermissions() {

        boolean wasNeeded = false;

        String wifiState = Manifest.permission.ACCESS_WIFI_STATE;

        int hasWifiStatePermission = ContextCompat.checkSelfPermission(this, wifiState);


        if (hasWifiStatePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, PermissionRequestCodes.ACCESS_WIFI);
            wasNeeded = true;
        } else {
            preferences.setBooleanKey(ApplicationPreferences.KEYS.ACCESS_WIFI_PERMISSION, true);
        }


        String changeWifiState = Manifest.permission.CHANGE_WIFI_STATE;

        int hasPermissionToChangeState = ContextCompat.checkSelfPermission(this, changeWifiState);

        if (hasPermissionToChangeState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, PermissionRequestCodes.CHANGE_WIFI);
            wasNeeded = true;
        } else {
            preferences.setBooleanKey(ApplicationPreferences.KEYS.CHANGE_WIFI_PERMISSION, true);
        }

        int hasCoarseLocalizationPermission = ContextCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasCoarseLocalizationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PermissionRequestCodes.COARSE_LOCALIZATION);
            wasNeeded = true;
        } else {
            preferences.setBooleanKey(ApplicationPreferences.KEYS.COARSE_LOCALIZATION, true);
        }

        return wasNeeded;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case PermissionRequestCodes.ACCESS_WIFI:
            case PermissionRequestCodes.CHANGE_WIFI:
            case PermissionRequestCodes.COARSE_LOCALIZATION:

                for (int i = 0; i < permissions.length; i++) {

                    if (Manifest.permission.ACCESS_COARSE_LOCATION.equalsIgnoreCase(permissions[i])

                            && PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                        preferences.setBooleanKey(ApplicationPreferences.KEYS.COARSE_LOCALIZATION, true);
                    }

                    if (Manifest.permission.ACCESS_WIFI_STATE.equalsIgnoreCase(permissions[i])
                            && PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                        preferences.setBooleanKey(ApplicationPreferences.KEYS.ACCESS_WIFI_PERMISSION, true);
                    }

                    if (Manifest.permission.CHANGE_WIFI_STATE.equalsIgnoreCase(permissions[i])

                            && PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                        preferences.setBooleanKey(ApplicationPreferences.KEYS.CHANGE_WIFI_PERMISSION, true);
                    }
                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void createPosition(String label) {
        HttpService service = (HttpService) servicesCache.get(Services.HTTP);

        service.newPosition(localization, new NewPositionRequest(label), new OnResponse<Position>() {
            @Override
            public void onSuccess(Position o) {
                getSupportFragmentManager()
                        .popBackStack();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "onFailure: Duplicate position error", throwable);
                Toast.makeText(context(), R.string.fnp_new_position_label_duplicate_input, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setScreenAlwaysOnFlag() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void clearScreenAlwaysOnFlag() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
