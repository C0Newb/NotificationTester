package com.cnco.notificationtester;

import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.drawable.IconCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;

import java.security.Security;
import java.util.Collections;

public class WallOfButtons extends AppCompatActivity {

    private static final String TAG = WallOfButtons.class.getSimpleName();
    NotificationUtils NotificationUtilities;

    NotificationCompat.MessagingStyle.Message[] Messages = new NotificationCompat.MessagingStyle.Message[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_of_buttons);

        NotificationUtilities = new NotificationUtils(getApplicationContext());

        long baseTime = System.currentTimeMillis();
        baseTime -= 1000;
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
    }

    // Messaging
    public void btnConversation_Click(View view) {
        NotificationUtilities.CreateNotificationChannel(NotificationUtilities.NotificationMessagingChannelId, "Messages", "Channel used to send sample messages.", NotificationManager.IMPORTANCE_HIGH);

        NotificationCompat.Builder msgNotification = NotificationUtilities.CreateMessageNotification(NotificationUtils.InlineReplyNotificationId, Messages, "Messaging Beeg Yoshi");

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NotificationUtils.InlineReplyNotificationId, msgNotification.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void btnConversationBubble_Click(View view) {
        String parent = NotificationUtilities.CreateNotificationChannel(NotificationUtilities.NotificationMessagingChannelId, "Messages", "Channel used to send sample messages.", NotificationManager.IMPORTANCE_HIGH);

        // Running Android version 8+, requires notification channel
        NotificationChannel channel = new NotificationChannel(String.valueOf(NotificationUtils.NotificationMessagingChannelId+1), "Messages with Yoshi", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Bubbled conversation");
        channel.setAllowBubbles(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            channel.setConversationId(parent, "Yoshi");
        }

        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);


        NotificationManager notificationManager = getSystemService(NotificationManager.class); // idk maybe
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder msgNotification = NotificationUtilities.CreateInlineMessageNotification(NotificationUtils.InlineReplyNotificationId, Messages, "Bubble Msg Beeg Yoshi");
        Intent target = new Intent(getApplicationContext(), ShowNotificationActivationDetails.class);
        PendingIntent bubbleIntent =
        PendingIntent.getActivity(getApplicationContext(), 0, target, PendingIntent.FLAG_MUTABLE);
        Person yoshi = new Person.Builder()
                    .setIcon(IconCompat.createWithResource(getApplicationContext(), R.mipmap.beegyoshi))
                    .setImportant(true)
                    .setName("Beeg Yoshi").build();
        msgNotification.setBubbleMetadata(new NotificationCompat.BubbleMetadata.Builder(bubbleIntent, IconCompat.createWithResource(getApplicationContext(), R.mipmap.beegyoshi))
                .setAutoExpandBubble(true)
                .setIntent(bubbleIntent)
                .setSuppressNotification(true)
                .setDesiredHeight(600)
                .build());

        // Show the notification
        notificationManager.notify(NotificationUtils.InlineReplyNotificationId, msgNotification.build());
    }

    public void btnInlineReply_Click(View view) {
        NotificationUtilities.CreateNotificationChannel(NotificationUtilities.NotificationMessagingChannelId, "Messages", "Channel used to send sample messages.", NotificationManager.IMPORTANCE_HIGH);

        NotificationCompat.Builder msgNotification = NotificationUtilities.CreateInlineMessageNotification(NotificationUtils.InlineReplyNotificationId, Messages, "Messaging Beeg Yoshi");


        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NotificationUtils.InlineReplyNotificationId, msgNotification.build());
    }


    // Full screen notifications (calls, alarms, timers)
    public void btnCall_Click(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Running Android version 8+, requires notification channel
            NotificationChannel channel = new NotificationChannel(String.valueOf(NotificationUtils.CallNotificationId), "Incoming calls", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Incoming audio and video call alerts.");
            channel.enableVibration(true);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE), new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build());


            NotificationManager notificationManager = getSystemService(NotificationManager.class); // idk maybe
            notificationManager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Notification.Builder call = NotificationUtilities.CreateCallNotificationNew(NotificationUtils.CallNotificationId, "Beeg Yoshi");

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NotificationUtils.CallNotificationId, call.build());
        } else {
            NotificationCompat.Builder call = NotificationUtilities.CreateCallNotification(NotificationUtils.CallNotificationId, "Beeg Yoshi");

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NotificationUtils.CallNotificationId, call.build());
        }
    }

    public void btnTimer_Click(View view) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
            .putExtra(AlarmClock.EXTRA_MESSAGE, "Testing Timer")
            .putExtra(AlarmClock.EXTRA_LENGTH, 5)
            .putExtra(AlarmClock.EXTRA_SKIP_UI, false);
        startActivity(intent);
    }


    // Misc
    public void btnProgressBar_Click(View view) {
        NotificationUtilities.CreateNotificationChannel(NotificationUtils.NotificationDefaultChannelId, "", "");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationCompat.Builder pBar = NotificationUtilities.CreateProgressBarNotification(NotificationUtils.ProgressBarNotificationId, NotificationUtils.NotificationDefaultChannelId, "Picture Download", "Download in progress");
        notificationManager.notify(NotificationUtils.ProgressBarNotificationId, pBar.build());

        new Thread(() -> {
            for (int current = 0; current <= 100; current += 5) {
                pBar.setProgress(100, current, false);
                // Displays the progress bar for the first time.
                notificationManager.notify(NotificationUtils.ProgressBarNotificationId, pBar.build());
                // Sleeps the thread, simulating an operation
                // that takes time
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.i(TAG, "Sleep failure ?? ");
                }
            }
            // When the loop is finished, updates the notification
            pBar.setOngoing(false);
            pBar.setOnlyAlertOnce(false);
            pBar.setContentText("Download complete").setProgress(0, 0, false);
            notificationManager.notify(NotificationUtils.ProgressBarNotificationId, pBar.build());
        }).start();
    }


    // Animated = updating
    private Thread AnimatedNotificationThread;
    private NotificationCompat.Builder AnimatedNotification;
    private boolean bKeepRunningAnimation = true;
    public void btnStartAnimated_Click(View view) {
        NotificationUtilities.CreateNotificationChannel(NotificationUtils.NotificationDefaultChannelId, "", "");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        AnimatedNotification = NotificationUtilities.CreateBaseNotification(NotificationUtils.AnimatedNotificationId, NotificationUtils.NotificationDefaultChannelId, "Animated", "|*========|");
        AnimatedNotification.setOnlyAlertOnce(true);
        AnimatedNotification.setOngoing(true); // Probably a good idea...
        notificationManager.notify(NotificationUtils.AnimatedNotificationId, AnimatedNotification.build());

        // Bounce right and left...
        bKeepRunningAnimation = true;
        AnimatedNotificationThread = new Thread(() -> {
            int pos = 0;
            boolean goRight = true;
            StringBuilder text = new StringBuilder("==========");
            while(bKeepRunningAnimation) {
                text.setCharAt(pos, '*');


                AnimatedNotification.setContentText("|" + text.toString() + "|");
                notificationManager.notify(NotificationUtils.AnimatedNotificationId, AnimatedNotification.build());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.i(TAG, "Sleep failure ?? ");
                }

                text.setCharAt(pos, '=');
                if (pos>=text.length()-1)
                    goRight = false;
                else if (pos<=0)
                    goRight = true;

                if (goRight)
                    pos++;
                else
                    pos--;
            }

            notificationManager.cancel(NotificationUtils.AnimatedNotificationId); // Cleanup
        });

        AnimatedNotificationThread.start();
    }

    public void btnStopAnimated_Click(View view) {
        bKeepRunningAnimation = false; // it'll stop, eventually...
    }


    // Persistent
    public void btnCreatePersistent_Click(View view) {
        NotificationUtilities.CreateNotificationChannel(NotificationUtils.NotificationLowImportanceChannelId, "Low importance", "Low importance notifications");

        NotificationCompat.Builder notification = NotificationUtilities.CreatePersistentNotification(NotificationUtils.PersistentNotificationId, NotificationUtils.NotificationLowImportanceChannelId, "Persistent example", "I'm not going anywhere.");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NotificationUtils.PersistentNotificationId, notification.build());
    }

    public void btnKillPersistent_Click(View view) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.cancel(NotificationUtils.PersistentNotificationId);
    }
}