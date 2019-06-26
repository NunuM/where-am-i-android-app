package me.nunum.whereami.service.notification;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import me.nunum.whereami.R;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.NotificationStorage;
import me.nunum.whereami.utils.AppConfig;

/**
 * Created by nuno on 03/06/2019.
 */

public class FireNotificationManager extends FirebaseMessagingService {

    private static final String TAG = FireNotificationManager.class.getSimpleName();

    private static final String NOTIFICATION_TYPE = "action";

    public static final String NEW_POSTS_TOPIC = "newPosts";
    public static final String NEW_ALGORITHM_TOPIC = "newPosts";

    public static boolean processNotificationData(final Context context, final Map<String, String> data, long sentTime) {
        boolean showToast = false;
        Log.i(TAG, "processNotificationData: Process notification");

        final String action = data.get(NOTIFICATION_TYPE);

        if (action != null && !action.isEmpty()) {
            try {
                MESSAGE_TYPE messageType = MESSAGE_TYPE.parse(Integer.valueOf(action));
                switch (messageType) {
                    case NEW_ALGORITHM:

                        final String newAlgorithmId = data.get("algorithmId");
                        final String newAlgorithmName = data.get("algorithmName");

                        if (newAlgorithmId != null
                                && newAlgorithmName != null) {
                            String message = context.getString(R.string.notification_new_algorithm, newAlgorithmName, newAlgorithmId);
                            NotificationStorage.persistNotification(context, message, sentTime);
                        }

                        showToast = true;
                        break;
                    case TRAINED_FINISHED:

                        final String trainedProviderId = data.get("algorithmProviderId");
                        final String localizationName = data.get("localizationName");

                        if (localizationName != null
                                && trainedProviderId != null) {
                            String message = context.getString(R.string.notification_new_model, trainedProviderId, localizationName);
                            NotificationStorage.persistNotification(context, message, sentTime);
                        }

                        showToast = true;
                        break;
                    case DELETE_ALGORITHM_PROVIDER:

                        String deletedAlgorithmName = data.get("algorithmName");

                        if (deletedAlgorithmName != null) {
                            String message = context.getString(R.string.notification_delete_algorithm, deletedAlgorithmName);
                            NotificationStorage.persistNotification(context, message, sentTime);
                        }

                        showToast = true;
                        break;
                    case UNKNOWN:

                    default:
                        break;
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "onMessageReceived: Action was not a integer", e);
            }
        } else {
            Log.i(TAG, "onMessageReceived: Not send action key:" + data);
        }

        return showToast;
    }

    @Override
    public void onNewToken(final String token) {
        super.onNewToken(token);

        Log.i(TAG, "onNewToken: Received new token: " + token);

        HttpService service = HttpService.create(getApplicationContext(), new Gson());
        service.updateDevice(new Device(token), new OnResponse<Device>() {
            @Override
            public void onSuccess(Device o) {
                Log.i(TAG, "onSuccess: updated FireBase token successfully:" + token);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, "onFailure: could not updated FireBase token:", throwable);
            }
        });
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(TAG, "onMessageReceived: Received a new notification from:" + remoteMessage.getFrom());

        final Map<String, String> data = remoteMessage.getData();
        final long sentTime = remoteMessage.getSentTime();

        if(processNotificationData(getApplicationContext(), data, sentTime)){
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(AppConfig.BROADCAST_NEW_NOTIFICATION_ACTION));
        }
    }

    public enum MESSAGE_TYPE {
        NEW_ALGORITHM, TRAINED_FINISHED, DELETE_ALGORITHM_PROVIDER, UNKNOWN;

        static MESSAGE_TYPE parse(int action) {
            if (action < MESSAGE_TYPE.values().length && action > 0) {
                return MESSAGE_TYPE.values()[action - 1];
            }
            return MESSAGE_TYPE.UNKNOWN;
        }
    }

}
