package com.cnco.notificationtester;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.RemoteInput;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShowNotificationActivationDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notification_activation_details);

        //String notificationId = getIntent().getStringExtra("NotificationId");
        //((TextView)findViewById(R.id.txtActivatedBy)).setText("Activated by notificationId: " + notificationId);
    }
}