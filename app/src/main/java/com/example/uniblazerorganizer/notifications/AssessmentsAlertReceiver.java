package com.example.uniblazerorganizer.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.uniblazerorganizer.DetailAssessmentActivity;
import com.example.uniblazerorganizer.R;

public class AssessmentsAlertReceiver extends BroadcastReceiver {

    private String channelId;
    private String channelName;

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        int assessmentId = intent.getIntExtra("assessmentId", -1);

        channelId = intent.getStringExtra("channelId");
        channelName = intent.getStringExtra("channelName");

        if (channelId == null) channelId = "default_channel";
        if (channelName == null) channelName = "General Notifications";

        Intent notificationIntent = new Intent(context, DetailAssessmentActivity.class);
        notificationIntent.putExtra("assessmentId", assessmentId); // pass the ID
        notificationIntent.setData(Uri.parse("custom://assessment/" + assessmentId)); // ensure uniquenes
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, assessmentId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel (for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId).setSmallIcon(R.drawable.outline_alarm_24).setContentTitle(title).setContentText(message).setContentIntent(pendingIntent).setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH);

        Log.d("AssessmentsAlertReceiver", "Received alarm: id " + assessmentId + " " + title + " - " + message);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
