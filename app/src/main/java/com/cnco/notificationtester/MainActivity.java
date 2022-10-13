package com.cnco.notificationtester;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public int LastNotificationId = 0;

    private ImageButton btnIcon;
    private ImageButton btnPicture;
    private IconCompat NotificationIcon;
    private Bitmap NotificationPicture;

    private NotificationUtils Global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnIcon = findViewById(R.id.btnIcon);
        btnPicture = findViewById(R.id.btnPicture);

        Global = new NotificationUtils(getApplicationContext());

        ((RadioGroup)findViewById(R.id.radStyleGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBasic) {
                    btnPicture.setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.lblPicture)).setVisibility(View.GONE);
                    ((Button)findViewById(R.id.btnResetPicture)).setVisibility(View.GONE);
                } else {
                    btnPicture.setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.lblPicture)).setVisibility(View.VISIBLE);
                    ((Button)findViewById(R.id.btnResetPicture)).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void btnIcon_Click(View view) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
    }
    public void btnPicture_Click(View view) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 2);//one can be replaced with any action code
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    NotificationIcon = IconCompat.createWithContentUri(selectedImage);
                    btnIcon.setImageURI(selectedImage);
                }

                break;

            case 2:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        NotificationPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btnPicture.setImageURI(selectedImage);
                }

                break;
        }
    }


    public void btnResetIcon_Click(View view) {
        NotificationIcon = IconCompat.createWithResource(getApplicationContext(), R.drawable.notificationicon);
        btnIcon.setImageResource(R.drawable.notificationicon);
    }
    public void btnResetPicture_Click(View view) {
        NotificationPicture = BitmapFactory.decodeResource(getResources(), R.drawable.notificationicon);
        btnPicture.setImageResource(R.drawable.notificationicon);
    }

    public void btnWallOfButtons_Click(View view) {
        // Build the "tap" intent
        Intent intent = new Intent(this, WallOfButtons.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        // With backStack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this); // Allows the back button to be useful
        stackBuilder.addNextIntentWithParentStack(intent);
        startActivity(intent);
    }

    public void btnSend_Click(View view) {
        // https://developer.android.com/develop/ui/views/notifications/build-notification#notify
        int notificationId = LastNotificationId++;

        // Need a channel to put this in.
        Global.CreateNotificationChannel();

        // Build the "tap" intent
        Intent intent = new Intent(this, ShowNotificationActivationDetails.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        intent.putExtra("NotificationId", notificationId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // With backStack
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this); // Allows the back button to be useful
        //stackBuilder.addNextIntentWithParentStack(intent);
        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(3, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        // Build the notification
        EditText txtTitle = findViewById(R.id.txtTitle);
        EditText txtDescription = findViewById(R.id.txtDescription);

        String notificationTitle = txtTitle.getText().toString().isEmpty()? "Test notification" : txtTitle.getText().toString();
        String notificationDescription = txtDescription.getText().toString().isEmpty()? "Test notification" + "\nNOTIFICATION ID: " + notificationId : txtDescription.getText().toString() + "\nNOTIFICATION ID: " + notificationId;
        IconCompat notificationIcon = (NotificationIcon == null)? IconCompat.createWithResource(getApplicationContext(), R.drawable.notificationicon) : NotificationIcon;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Testing");
        builder.setSmallIcon(notificationIcon);                     // ... the icon
        builder.setContentTitle(notificationTitle);                 // ... the title
        builder.setContentText(notificationDescription);            // ... the contents

        if (((RadioButton)findViewById(R.id.radBasic)).isChecked())
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationDescription)); // Big (expanded) text
        else if (((RadioButton)findViewById(R.id.radBigImage)).isChecked())
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(NotificationPicture)); // Big picture

        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);   // Priority
        builder.setContentIntent(pendingIntent);                    // Click intent (call this on click)
        builder.setAutoCancel(true);                                // Remove on click


        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());

        Snackbar.make(view, "Notification sent, ID: " + String.valueOf(notificationId), 500)
                .show();
    }
}