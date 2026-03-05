package com.example.anchor;

import java.net.URI;

import java.time.Instant;

public class NoteController {

    // Properties

    public Note currentNote;
    public NoteStore noteStore;
    public MediaStore mediaStore;
    public TagStore tagStore;
    public LocationService locationService;
    public ReminderScheduler reminderScheduler;


    // Methods
    public Note createBlank(String title) {
        // TODO save to noteStore and update note id.
        Note note = new Note();
        note.title = title;
        this.currentNote = note;
        return note;
    }

    public void save(Note n) {
        if (noteStore != null) {
            noteStore.save(n);
        }
    }

    public void delete(long noteId) {
        if (noteStore != null) {
            noteStore.delete(noteId);
        }
    }

    public PhotoAttachment attachPhoto(Note n, URI uri, String mime, Integer w, Integer h) {
        PhotoAttachment photo = new PhotoAttachment();
        photo.uri = uri;
        photo.mimetype = mime;
        photo.width = w;
        photo.height = h;

        n.attach(photo);
        n.hasPhoto = true;

        if (mediaStore != null) {
            mediaStore.savePhoto(photo);
        }

        return photo;
    }

    public AudioAttachment attachAudio(Note n, URI uri, String mime, Long durationMs) {
        AudioAttachment audio = new AudioAttachment();
        audio.uri = uri;
        audio.mimetype = mime;
        audio.durationMs = durationMs;

        n.attach(audio);
        n.hasAudio = true;

        if (mediaStore != null) {
            mediaStore.saveAudio(audio);
        }

        return audio;
    }

    public boolean removeAttachment(Note n, long attachmentId) {
        boolean removed = n.detachById(attachmentId);

        if (removed) {
            n.recomputeMediaFlags();

            if (mediaStore != null) {
                mediaStore.deleteAttachment(attachmentId);
            }
        }

        return removed;
    }

    public void addTag(Note n, long tagId) {
        n.addTag(tagId);

        if (tagStore != null) {
            tagStore.linkNoteToTag(n.id, tagId);
        }
    }

    public void removeTag(Note n, long tagId) {
        n.removeTag(tagId);

        if (tagStore != null) {
            tagStore.unlinkNoteFromTag(n.id, tagId);
        }
    }

    public void togglePin(Note n) {
        n.togglePinned();
        save(n);
    }

    public void setCurrentLocation(Note n) {
        if (locationService != null) {
            GeoTag location = locationService.getCurrentLocation();
            n.setLocation(location);
            n.hasLocation = true;
            save(n);
        }
    }

    public void removeLocation(Note n) {
        n.removeLocation();
        n.hasLocation = false;
        save(n);
    }

    public void setTimeReminder(Note n, Instant triggerAt, RepeatRule repeat) {
        TimeReminder reminder = new TimeReminder(n.id, triggerAt, repeat);

        n.setTimeReminder(reminder);

        if (reminderScheduler != null) {
            reminderScheduler.scheduleTimeReminder(n.id, reminder);
        }

        save(n);
    }

    public void clearTimeReminder(Note n) {
        n.clearTimeReminder();

        if (reminderScheduler != null) {
            reminderScheduler.cancelTimeReminder(n.id);
        }

        save(n);
    }

    public void setGeofenceReminder(Note n, double lat, double lon, Float radiusMeters,
                                    GeofenceTransition transition, String geofenceId) {
        GeofenceReminder reminder = new GeofenceReminder();
        reminder.centerLat = lat;
        reminder.centerLon = lon;
        reminder.radiusMeters = radiusMeters;
        reminder.transitionType = transition;
        reminder.geofenceId = geofenceId;

        n.setGeofenceReminder(reminder);

        if (reminderScheduler != null) {
            reminderScheduler.scheduleGeofenceReminder(n.id, reminder);
        }

        save(n);
    }

    public void clearGeofenceReminder(Note n) {
        n.clearGeofenceReminder();

        if (reminderScheduler != null) {
            reminderScheduler.cancelGeofenceReminder(n.id);
        }

        save(n);
    }
}

