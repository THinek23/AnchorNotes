// app/src/main/java/com/example/anchor/ReminderReceiver.java
package com.example.anchor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.time.Instant;

public class ReminderReceiver extends BroadcastReceiver {

    // Use constants for intent extras to avoid typos
    public static final String EXTRA_NOTE_ID = "com.example.anchor.NOTE_ID";
    public static final String EXTRA_INSTANT_SECONDS = "com.example.anchor.INSTANT_SECONDS";
    public static final String EXTRA_INSTANT_NANOS = "com.example.anchor.INSTANT_NANOS";



    private static final String CHANNEL_ID = "REMINDER_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a Notification Channel (required for Android 8.0 Oreo and higher)

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Note Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Shows notifications for note reminders");
        notificationManager.createNotificationChannel(channel);

        // Extract data from the intent
        long noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1);

        if (noteId == -1) {
            return; // Invalid note ID, do nothing
        }

        // Create the notification
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // IMPORTANT: Create this icon
                .setContentTitle("Reminder")
                .setContentText("Note id "+ noteId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true) // Dismiss notification on tap
                .build();

        // Show the notification. The note ID is used as the notification ID.
        notificationManager.notify((int) noteId, notification);

        // TODO: (Optional) Handle recurring reminders by rescheduling the next one here.
        long instantSeconds = intent.getLongExtra(EXTRA_INSTANT_SECONDS, -1);
        int instantNanos = intent.getIntExtra(EXTRA_INSTANT_NANOS, -1);
        if (instantSeconds == -1 || instantNanos == -1)
            return;
        Instant instant = Instant.ofEpochSecond(instantSeconds, instantNanos);
        MainActivity.reminderController.onTimeReminderFired(noteId, instant);
    }
}
