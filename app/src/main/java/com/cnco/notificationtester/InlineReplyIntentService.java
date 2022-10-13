package com.cnco.notificationtester;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.drawable.IconCompat;

import java.util.LinkedList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class InlineReplyIntentService extends IntentService {

    private static final String TAG = InlineReplyIntentService.class.getSimpleName();

    public static final String ACTION_READ = "action_read";
    public static final String ACTION_REPLY = "action_reply";
    private static List<CharSequence> responseHistory = new LinkedList<>();

    public InlineReplyIntentService() {
        super("InlineReplyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if(ACTION_READ.equals(intent.getAction())) {
            Log.i(TAG, "Notification marked READ");
            responseHistory.clear();
            notificationManager.cancel(NotificationUtils.InlineReplyNotificationId);
            return;
        }

        CharSequence reply = null;
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            reply = remoteInput.getCharSequence(NotificationUtils.NotificationKeyInlineReply);
            responseHistory.add(0, reply);
            Log.i(TAG, reply.toString());
        } else {
            Log.i(TAG, "Reply sent.");
            Intent activityIntent = new Intent(this, ShowNotificationActivationDetails.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //cancel the notification since we starting an Activity
            notificationManager.cancel(NotificationUtils.InlineReplyNotificationId);
            //startActivity(activityIntent);
            return;
        }

        // Update notification
        if(!responseHistory.isEmpty()) {
            CharSequence[] history = new CharSequence[responseHistory.size()];
            this.UpdateInlineReplyNotification("Response has been processed", responseHistory.toArray(history));
        } else {
            this.UpdateInlineReplyNotification("Response has been processed");
        }

    }

    NotificationCompat.MessagingStyle.Message[] Messages = new NotificationCompat.MessagingStyle.Message[4];

    public void UpdateInlineReplyNotification(String message) {
        UpdateInlineReplyNotification(message, null);
    }
    private void UpdateInlineReplyNotification(String message, CharSequence[] history) {
        long baseTime = System.currentTimeMillis();
        baseTime -= 1000;
        Messages = new NotificationCompat.MessagingStyle.Message[5];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            Person yoshi = new Person.Builder()
                    .setIcon(IconCompat.createWithResource(getApplicationContext(), R.mipmap.beegyoshi))
                    .setImportant(true)
                    .setName("Beeg Yoshi").build();

            Messages[0] = new NotificationCompat.MessagingStyle.Message("Hey? How are you?!?", baseTime-3000, yoshi);
            Messages[1] = new NotificationCompat.MessagingStyle.Message("I'm doing good, you?", baseTime-2000, "You");
            Messages[2] = new NotificationCompat.MessagingStyle.Message("ahh you know, just sitting here.", baseTime-1000, yoshi);
            Messages[3] = new NotificationCompat.MessagingStyle.Message("haha so ORIGINAL and funny!", baseTime, "You");
        } else {
            Messages[0] = new NotificationCompat.MessagingStyle.Message("Hey? How are you?!?", baseTime-3000, "Beeg Yoshi");
            Messages[1] = new NotificationCompat.MessagingStyle.Message("I'm doing good, you?", baseTime-2000, "You");
            Messages[2] = new NotificationCompat.MessagingStyle.Message("ahh you know, just sitting here.", baseTime-1000, "Beeg Yoshi");
            Messages[3] = new NotificationCompat.MessagingStyle.Message("haha so ORIGINAL and funny!", baseTime, "You");
        }

        Context context = getApplicationContext();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationUtils(getApplicationContext()).CreateInlineMessageNotification(NotificationUtils.InlineReplyNotificationId, Messages, "Beeg Yoshi");
        builder.setContentText(message);
        if(history != null) {
            builder.setRemoteInputHistory(history);
        }

        Log.i(TAG, "Inline notification updated.");

        notificationManager.notify(NotificationUtils.InlineReplyNotificationId, builder.build());
    }
}