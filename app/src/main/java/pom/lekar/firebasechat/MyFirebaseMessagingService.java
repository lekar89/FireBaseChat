package pom.lekar.firebasechat;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by lekar on 02.05.17.
 */

public class MyFirebaseMessagingService {
    private static final String TAG = "MyFMService";

    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
    }

}
