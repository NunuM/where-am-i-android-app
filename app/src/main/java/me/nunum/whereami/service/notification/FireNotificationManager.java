package me.nunum.whereami.service.notification;


import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by nuno on 03/06/2019.
 */

public class FireNotificationManager extends FirebaseMessagingService {

    public enum MESSAGE_TYPE{
        TRAINING_FINISHED, PROVIDER_DELETED, PREDICTION_RESULT;

        public boolean hasPayload(){
            return true;
        }
    }


    private static final String TAG = FireNotificationManager.class.getSimpleName();


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i(TAG, "onNewToken:" + s);

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Integer action = Integer.valueOf(remoteMessage.getData().get("action"));
        MESSAGE_TYPE[] values = MESSAGE_TYPE.values();

        MESSAGE_TYPE value = values[action];
        Log.i(TAG, "onMessageReceived: " + remoteMessage.getMessageId());
    }
}
