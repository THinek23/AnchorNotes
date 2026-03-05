package com.example.anchor;

import java.time.Instant;

public class ReminderController {

    // Properties
    public ReminderScheduler reminderScheduler;
    public RelevantStore relevantStore;
    public NoteStore noteStore;

    // Methods
    public void onTimeReminderFired(long noteId, Instant firedAt) {
        if (noteStore != null) {
            Note note = noteStore.findById(noteId);

            if (note != null) {
                // Handle time reminder fired
                if (relevantStore != null) {
                    // TODO check relevantStore methods and functions
                    relevantStore.markAsRelevant(noteId, firedAt);
                }

                // Check if reminder should repeat
                if (note.timeReminder != null && note.timeReminder.repeatRule != null) {
                    // Schedule next occurrence based on repeat rule
                    note.timeReminder.markFired(firedAt);
                    if (reminderScheduler != null) {
                        reminderScheduler.scheduleTimeReminder(noteId, note.timeReminder);
                    }
                } else {
                    // Clear one-time reminder
                    note.clearTimeReminder();
                    noteStore.save(note);
                }
            }
        }
    }

    public void onGeofenceEnter(long noteId, Instant enteredAt) {
        if (noteStore != null) {
            Note note = noteStore.findById(noteId);

            if (note != null && note.geoFenceReminder != null) {
                // Handle geofence enter event
                if (note.geoFenceReminder.transitionType == GeofenceTransition.ENTER
                        || note.geoFenceReminder.transitionType == GeofenceTransition.BOTH) {

                    if (relevantStore != null) {
                        relevantStore.markAsRelevant(noteId, enteredAt);
                    }
                }
            }
        }
    }

    public void onGeofenceExit(long noteId, Instant exitedAt) {
        if (noteStore != null) {
            Note note = noteStore.findById(noteId);

            if (note != null && note.geoFenceReminder != null) {
                // Handle geofence exit event
                if (note.geoFenceReminder.transitionType == GeofenceTransition.EXIT
                        || note.geoFenceReminder.transitionType == GeofenceTransition.BOTH) {

                    if (relevantStore != null) {
                        relevantStore.markAsRelevant(noteId, exitedAt);
                    }
                }
            }
        }
    }
}

