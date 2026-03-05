package com.example.anchor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * RelevantNoteEntry - Represents a note that is currently relevant
 * Tracks why it's relevant, when it became relevant, and when it expires
 */
public class RelevantNoteEntry extends BaseEntity {

    public long noteId;
    public RelevantSource source;
    public Instant insertedAt;
    public Instant expiresAt;

    /**
     * Default constructor
     */
    public RelevantNoteEntry() {
        this.insertedAt = Instant.now();
    }

    /**
     * Constructor with all fields
     */
    public RelevantNoteEntry(long noteId, RelevantSource source, Instant insertedAt, Instant expiresAt) {
        this.noteId = noteId;
        this.source = source;
        this.insertedAt = insertedAt;
        this.expiresAt = expiresAt;
    }

    /**
     * Create a relevant entry for a time-based reminder
     * Expires after 24 hours by default
     *
     * @param noteId The ID of the note
     * @param firedAt The time when the reminder fired
     * @return A new RelevantNoteEntry
     */
    public static RelevantNoteEntry timeWindow(long noteId, Instant firedAt) {
        RelevantNoteEntry entry = new RelevantNoteEntry();
        entry.noteId = noteId;
        entry.source = RelevantSource.TIME_REMINDER;
        entry.insertedAt = firedAt;

        // Time reminders expire after 24 hours
        entry.expiresAt = firedAt.plus(24, ChronoUnit.HOURS);

        return entry;
    }

    /**
     * Create a relevant entry for a geofence entry
     * Expires after 4 hours by default (shorter than time reminders)
     *
     * @param noteId The ID of the note
     * @param entered The time when the geofence was entered
     * @return A new RelevantNoteEntry
     */
    public static RelevantNoteEntry geofence(long noteId, Instant entered) {
        RelevantNoteEntry entry = new RelevantNoteEntry();
        entry.noteId = noteId;
        entry.source = RelevantSource.GEOFENCE_ENTER;
        entry.insertedAt = entered;

        // Geofence entries expire after 4 hours
        entry.expiresAt = entered.plus(4, ChronoUnit.HOURS);

        return entry;
    }

    /**
     * Create a relevant entry for a geofence exit
     *
     * @param noteId The ID of the note
     * @param exited The time when the geofence was exited
     * @return A new RelevantNoteEntry
     */
    public static RelevantNoteEntry geofenceExit(long noteId, Instant exited) {
        RelevantNoteEntry entry = new RelevantNoteEntry();
        entry.noteId = noteId;
        entry.source = RelevantSource.GEOFENCE_EXIT;
        entry.insertedAt = exited;

        // Exit reminders expire after 2 hours
        entry.expiresAt = exited.plus(2, ChronoUnit.HOURS);

        return entry;
    }

    /**
     * Create a relevant entry with custom expiration
     *
     * @param noteId The ID of the note
     * @param source The source of relevance
     * @param insertedAt When it became relevant
     * @param expiresAt When it should expire
     * @return A new RelevantNoteEntry
     */
    public static RelevantNoteEntry custom(long noteId, RelevantSource source,
                                           Instant insertedAt, Instant expiresAt) {
        RelevantNoteEntry entry = new RelevantNoteEntry();
        entry.noteId = noteId;
        entry.source = source;
        entry.insertedAt = insertedAt;
        entry.expiresAt = expiresAt;
        return entry;
    }

    /**
     * Check if this entry has expired
     *
     * @param now The current time to check against
     * @return true if expired, false otherwise
     */
    public boolean isExpired(Instant now) {
        if (expiresAt == null) {
            // If no expiration set, never expires
            return false;
        }

        return now.isAfter(expiresAt);
    }

    /**
     * Check if this entry is still active (not expired)
     *
     * @param now The current time to check against
     * @return true if active, false if expired
     */
    public boolean isActive(Instant now) {
        return !isExpired(now);
    }

    /**
     * Get the time remaining until expiration
     *
     * @param now The current time
     * @return Duration in seconds, or -1 if expired or no expiration
     */
    public long getSecondsUntilExpiration(Instant now) {
        if (expiresAt == null) {
            return -1;
        }

        if (isExpired(now)) {
            return 0;
        }

        return now.until(expiresAt, ChronoUnit.SECONDS);
    }

    /**
     * Get the age of this entry in seconds
     *
     * @param now The current time
     * @return Age in seconds
     */
    public long getAgeInSeconds(Instant now) {
        if (insertedAt == null) {
            return 0;
        }

        return insertedAt.until(now, ChronoUnit.SECONDS);
    }

    /**
     * Extend the expiration time by a certain number of hours
     *
     * @param hours Number of hours to extend
     */
    public void extendExpiration(int hours) {
        if (expiresAt != null) {
            expiresAt = expiresAt.plus(hours, ChronoUnit.HOURS);
        }
    }

    /**
     * Set expiration to never expire
     */
    public void setNeverExpires() {
        this.expiresAt = null;
    }

    @Override
    public String toString() {
        return "RelevantNoteEntry{" +
                "id=" + id +
                ", noteId=" + noteId +
                ", source=" + source +
                ", insertedAt=" + insertedAt +
                ", expiresAt=" + expiresAt +
                ", expired=" + isExpired(Instant.now()) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RelevantNoteEntry that = (RelevantNoteEntry) obj;
        return noteId == that.noteId && source == that.source;
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(noteId);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }
}