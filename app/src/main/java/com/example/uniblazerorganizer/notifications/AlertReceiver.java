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
import com.example.uniblazerorganizer.DetailCourseActivity;
import com.example.uniblazerorganizer.HomeActivity;
import com.example.uniblazerorganizer.R;

import java.util.HashMap;
import java.util.Map;

public class AlertReceiver extends BroadcastReceiver {

    private String channelId = "default_channel";
    private String channelName = "Default Notifications";

    @Override
    public void onReceive(Context context, Intent intent) {
        Map<String, Class<?>> activityMap = new HashMap<>();
        activityMap.put("Default", HomeActivity.class);
        activityMap.put("Course", DetailCourseActivity.class);
        activityMap.put("Assessment", DetailAssessmentActivity.class);


        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        int objectId = intent.getIntExtra("objectId", -1);
        String objectType = intent.getStringExtra("objectType");

        if (objectType != null) {
            channelId = objectType.toLowerCase() + "_channel";
            channelName = objectType + " Alerts";
        } else {
            objectType = "Default";
        }

        Class<?> targetClass = activityMap.get(objectType);


        Intent notificationIntent = new Intent(context, targetClass);
        notificationIntent.putExtra(objectType.toLowerCase() + "Id", objectId); // pass the ID
        notificationIntent.setData(Uri.parse("custom://" + objectType.toLowerCase() + "/" + objectId)); // ensure uniquenes
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, objectId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel (for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId).setSmallIcon(R.drawable.outline_alarm_24).setContentTitle(title).setContentText(message).setContentIntent(pendingIntent).setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH);

        Log.d("AlertReceiver", "Received alarm: id " + objectId + " " + title + " - " + message);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
