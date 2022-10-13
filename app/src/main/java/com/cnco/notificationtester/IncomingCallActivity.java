package com.cnco.notificationtester;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class IncomingCallActivity extends AppCompatActivity {

    private static final String TAG = IncomingCallActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
    }

    public void btnAccept_Click(View view) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Log.i(TAG, "Call accepted");
        notificationManager.cancel(NotificationUtils.CallNotificationId);
        finish();
    }
    public void btnDeny_Click(View view) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Log.i(TAG, "Call declined");
        notificationManager.cancel(NotificationUtils.CallNotificationId);
        finish();
    }
}