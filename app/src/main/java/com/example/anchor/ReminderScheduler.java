package com.example.anchor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderScheduler {
    private final Context context;
    private final AlarmManager alarmManager;

    public ReminderScheduler(Context context) {
        this.context = context.getApplicationContext();
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Schedules the next occurrence of a time-based reminder.
     * @param noteId The unique ID of the note.
     * @param reminder The TimeReminder object containing reminder details.
     */
    public void scheduleTimeReminder(long noteId, TimeReminder reminder) {
        long triggerAtMillis = reminder.triggerAt.toEpochMilli();

        // Ensure the trigger time is in the future
        if (triggerAtMillis < System.currentTimeMillis()) {
            // Optional: You could add logic here for recurring reminders to find the next valid time.
            // For a one-time reminder, we simply don't schedule it if it's in the past.
            return;
        }

        // Create an intent that will be broadcasted when the alarm goes off
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.EXTRA_NOTE_ID, noteId);
        intent.putExtra(ReminderReceiver.EXTRA_INSTANT_SECONDS, reminder.triggerAt.getEpochSecond());
        intent.putExtra(ReminderReceiver.EXTRA_INSTANT_NANOS, reminder.triggerAt.getNano());


        // A PendingIntent is a token that gives another application the right to perform
        // an action on your application's behalf.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) noteId, // Use the note ID as a unique request code to update/cancel it later
                intent,
                // FLAG_UPDATE_CURRENT will update the extra data if the alarm is rescheduled
                // FLAG_IMMUTABLE is required for newer Android versions for security
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Check for permission to schedule exact alarms (required on Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Permission is not granted. You should ask the user to grant it.
                // For now, we will log or skip.
                return;
            }
        }

        // Schedule the alarm. RTC_WAKEUP ensures the alarm fires even if the device is asleep.
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
        );
    }

    public void cancelTimeReminder(long id) {

    }

    public void cancelGeofenceReminder(long id) {

    }

    public void scheduleGeofenceReminder(long id, GeofenceReminder reminder) {

    }
}


