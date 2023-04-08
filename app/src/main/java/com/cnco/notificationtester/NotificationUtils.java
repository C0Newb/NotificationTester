package com.cnco.notificationtester;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.os.Build;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.drawable.IconCompat;


public class NotificationUtils {

    private Context Context;


    // Default channel id
    public static final String NotificationDefaultChannelId = "Testing";
    // Notification channel ids
    public static final String NotificationMaxImportanceChannelId = "MaxImportance";
    public static final String NotificationHighImportanceChannelId = "HighImportance";
    public static final String NotificationDefaultImportanceChannelId = "DefaultChannel";
    public static final String NotificationLowImportanceChannelId = "LowImportance";
    public static final String NotificationMinImportanceChannelId = "MinImportance";
    public static final String NotificationUnspecifiedImportanceChannelId = "UnspecifiedImportance";
    public static final String NotificationMessagingChannelId = "MessagingChannel";
    public static final String NotificationFullScreenChannelId = "FullScreenChannel";
    public static final String NotificationCallsChannelId = "IncomingCalls";

    // Action keys
    public static final String NotificationKeyInlineReply = "TextReply";

    // Notification ids
    public static final int InlineReplyNotificationId = 1002;
    public static final int CallNotificationId = 1001;
    public static final int ProgressBarNotificationId = 1010;
    public static final int AnimatedNotificationId = 1011;
    public static final int PersistentNotificationId = 1012;


    public NotificationUtils(Context context) {
        this.Context = context;
    }

    /**
     * Creates a new notification channel for the application
     * @param id Notification channel ID (used when pushing notifications)
     * @param name Friendly name user sees in settings
     * @param description Friendly description user sees in settings
     * @param importance See https://developer.android.com/reference/android/app/NotificationManager#IMPORTANCE_DEFAULT. Either DEFAULT, HIGH, LOW, MAX, MIN, NONE, UNSPECIFIED.
     */
    public String CreateNotificationChannel(String id, String name, String description, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Running Android version 8+, requires notification channel
            id = (id.isEmpty())? NotificationDefaultChannelId : id;
            name = (name.isEmpty())? "Testing channel" : name;
            description = (description.isEmpty())? "Default notification channel to test on." : description;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = this.Context.getSystemService(NotificationManager.class); // idk maybe
            notificationManager.createNotificationChannel(channel);
            return channel.getId();
        }
        return "";
    }

    public void CreateNotificationChannel(String id, String name, String description) {
        CreateNotificationChannel(id, name, description, NotificationManager.IMPORTANCE_DEFAULT);
    }

    public void CreateNotificationChannel() {
        CreateNotificationChannel("Testing", "Testing channel", "Default notification channel to test on.");
    }


    public NotificationCompat.Builder CreateBaseNotification(int notificationId, String channelId, String title, String subText, IconCompat icon) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Context.getApplicationContext(), channelId);
        builder.setContentTitle(title);
        builder.setContentText(subText);
        builder.setSmallIcon((icon == null)? IconCompat.createWithResource(Context.getApplicationContext(), R.drawable.notificationicon) : icon);

        return builder;
    }

    public NotificationCompat.Builder CreateBaseNotification(int notificationId, String channelId, String title, String subText) {
        return CreateBaseNotification(notificationId, channelId, title, subText, IconCompat.createWithResource(Context.getApplicationContext(), R.drawable.notificationicon));
    }


    public NotificationCompat.Builder CreatePersistentNotification(int notificationId, String channelId, String title, String subText) {
        NotificationCompat.Builder notification = CreateBaseNotification(notificationId, channelId, title, subText);
        notification.setPriority(NotificationCompat.PRIORITY_LOW);
        notification.setCategory(NotificationCompat.CATEGORY_STATUS);
        notification.setOngoing(true);

        return notification;
    }

    // Progressbar
    public NotificationCompat.Builder CreateProgressBarNotification(int notificationId, String channelId, String title, String subText) {
        NotificationCompat.Builder builder = CreateBaseNotification(notificationId, channelId, title, subText);
        builder.setPriority(NotificationCompat.PRIORITY_LOW);
        builder.setCategory(NotificationCompat.CATEGORY_STATUS);
        builder.setOnlyAlertOnce(true);
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);

        // Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);

        return builder;
    }

    // Messaging
    public NotificationCompat.Builder CreateMessageNotification(int notificationId, @NonNull NotificationCompat.MessagingStyle.Message[] messages, String conversationTitle) {
        NotificationCompat.Builder msgNotification = CreateBaseNotification(notificationId, NotificationMessagingChannelId, "New message", "Message received from Yoshi.");


        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("You").setConversationTitle(conversationTitle);
        for (NotificationCompat.MessagingStyle.Message message : messages) {
            messagingStyle.addMessage(message); // Add each reply
        }
        msgNotification.setStyle(messagingStyle); // Apply messages

        msgNotification.setPriority(NotificationCompat.PRIORITY_HIGH);  // Priority
        //builder.setContentIntent(pendingIntent);                      // Click intent (call this on click)
        msgNotification.setAutoCancel(true);                            // Remove on click
        msgNotification.setColorized(true);
        msgNotification.setOnlyAlertOnce(true);
        msgNotification.setColor(Color.argb(255, 150, 255, 150));
        msgNotification.setCategory(NotificationCompat.CATEGORY_MESSAGE);

        msgNotification.setWhen(System.currentTimeMillis()-10000);

        return msgNotification;
    }

    public NotificationCompat.Builder CreateInlineMessageNotification(int notificationId, NotificationCompat.MessagingStyle.Message[] messages, String conversationTitle) {
        NotificationCompat.Builder notification = CreateMessageNotification(notificationId, messages, conversationTitle);

        Intent replyIntent;
        PendingIntent replyPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //we are running Nougat
            //the intent should start a service, so that the response can be handled in the background
            replyIntent = new Intent(Context, InlineReplyIntentService.class);
            replyPendingIntent = PendingIntent.getService(Context, 0, replyIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            //we are running a version prior to Nougat
            //There will be no response included with intent, so we want to start an Activity to allow
            //the user to enter input. This demo app does not have that capability, but in the real world
            //our Activity ought to allow that
            replyIntent = new Intent(Context, MainActivity.class);
            replyPendingIntent = PendingIntent.getActivity(Context, 0, replyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        replyIntent.setAction(InlineReplyIntentService.ACTION_READ);

        // Build the Action for an inline response, which includes a RemoteInput
        RemoteInput remoteInput = new RemoteInput.Builder(NotificationKeyInlineReply)
                .setLabel("Type your message...") // The hint on the input
                .build();
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_input_get, "Reply", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true) // Allow auto generated replies
                        .build();
        notification.addAction(action); // Add button

        // "Mark as read" button
        Intent doneIntent = new Intent(Context, InlineReplyIntentService.class);
        doneIntent.setAction(InlineReplyIntentService.ACTION_READ);
        PendingIntent donePendingIntent = PendingIntent.getService(Context, 0, doneIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action doneAction = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_close_clear_cancel, "Mark as read", donePendingIntent).build();
        notification.addAction(doneAction); // Add button

        return notification;
    }



    // Fullscreen ones
    @RequiresApi(api = Build.VERSION_CODES.S)
    public Notification.Builder CreateCallNotificationNew(int notificationId, String callerName) {
        // Decline
        Intent declineIntent = new Intent(Context, CallIntentService.class);
        declineIntent.setAction(CallIntentService.ACTION_DENY);
        PendingIntent declinePendingIntent = PendingIntent.getService(Context, 0, declineIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

        // Accept
        Intent acceptIntent = new Intent(Context, CallIntentService.class);
        acceptIntent.setAction(CallIntentService.ACTION_ACCEPT);
        PendingIntent acceptPendingIntent = PendingIntent.getService(Context, 0, acceptIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

        Person yoshi = new Person.Builder()
                    .setIcon(Icon.createWithResource(Context.getApplicationContext(), R.mipmap.beegyoshi))
                    .setImportant(true)
                    .setName("Beeg Yoshi").build();
        Notification.Builder call = new Notification.Builder(Context, NotificationCallsChannelId);
        call.setContentTitle("Incoming call");
        call.setContentText("Beeg Yoshi is calling");
        call.setSmallIcon(Icon.createWithResource(Context.getApplicationContext(), R.drawable.notificationicon));
        call.setStyle(Notification.CallStyle.forIncomingCall(yoshi, declinePendingIntent, acceptPendingIntent));

        Intent fullscreen = new Intent(Context, IncomingCallActivity.class);
        PendingIntent fullscreenPending = PendingIntent.getActivity(Context, 1, fullscreen, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        call.setFullScreenIntent(fullscreenPending, true);

        return call;
    }

    public NotificationCompat.Builder CreateCallNotification(int notificationId, String callerName) {
        NotificationCompat.Builder call = CreateBaseNotification(notificationId, NotificationCallsChannelId, "Incoming call", "There's an incoming call");
        call.setLargeIcon(BitmapFactory.decodeResource(Context.getResources(), R.mipmap.beegyoshi));
        call.setOngoing(true);
        call.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        call.setCategory(NotificationCompat.CATEGORY_CALL);


        // Decline
        Intent declineIntent = new Intent(Context, CallIntentService.class);
        declineIntent.setAction(CallIntentService.ACTION_DENY);
        PendingIntent declinePendingIntent = PendingIntent.getService(Context, 0, declineIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        call.addAction(new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_close_clear_cancel, "Decline", declinePendingIntent).build()); // Add button

        // Accept
        Intent acceptIntent = new Intent(Context, CallIntentService.class);
        acceptIntent.setAction(CallIntentService.ACTION_ACCEPT);
        PendingIntent acceptPendingIntent = PendingIntent.getService(Context, 0, acceptIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        call.addAction(new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_call, "Accept", acceptPendingIntent).build());


        Intent fullscreen = new Intent(Context, IncomingCallActivity.class);
        PendingIntent fullscreenPending = PendingIntent.getActivity(Context, 1, fullscreen, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        call.setFullScreenIntent(fullscreenPending, true);
        return call;

    }
}
