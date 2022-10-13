package com.cnco.notificationtester;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CallIntentService extends IntentService {
    private static final String TAG = CallIntentService.class.getSimpleName();

    public static final String ACTION_ACCEPT = "action_accept";
    public static final String ACTION_DENY = "action_deny";

    public CallIntentService() {
        super("CallIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if(ACTION_ACCEPT.equals(intent.getAction())) {
            Log.i(TAG, "Call accepted");
            notificationManager.cancel(NotificationUtils.CallNotificationId);
            return;
        }

        if(ACTION_DENY.equals(intent.getAction())) {
            Log.i(TAG, "Call declined");
            notificationManager.cancel(NotificationUtils.CallNotificationId);
            return;
        }
    }
}