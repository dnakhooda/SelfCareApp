package com.example.selfcare;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TaskNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Build Task by setting icon, setting title, setting text, and putting priority on to notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "taskMessages")
                .setSmallIcon(R.mipmap.ic_icon)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText("It is time to " + intent.getStringExtra("title"))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(intent.getIntExtra("id", 0), builder.build());
    }
}
