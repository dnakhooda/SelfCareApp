package com.example.selfcare;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class DailyNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Build Task by setting icon, setting title, setting text, and putting priority on to notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "dailyMessage")
                .setSmallIcon(R.mipmap.ic_icon)
                .setContentTitle("Daily Message")
                .setContentText("Come to the Self Care app to see your daily message!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}
