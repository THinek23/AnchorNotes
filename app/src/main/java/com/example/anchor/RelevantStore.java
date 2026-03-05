package com.example.anchor;

import android.content.Context;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * RelevantStore - Manages relevant notes storage
 * Tracks which notes are currently relevant and why
 */
public class RelevantStore {

    private AppDatabase db;
    private static final String TAG = "RelevantStore";

    /**
     * Constructor
     */
    public RelevantStore(Context context) {
        this.db = AppDatabase.getDatabase(context);
    }

    /**
     * Mark a note as relevant
     * Creates a new relevant entry with default expiration based on source
     *
     * @param noteId The ID of the note to mark as relevant
     * @param firedAt The time when it became relevant
     */
    public void markAsRelevant(long noteId, Instant firedAt) {
        // Create a time-based relevant entry (24 hour expiration)
        RelevantNoteEntry entry = RelevantNoteEntry.timeWindow(noteId, firedAt);
        insert(entry);
    }

    /**
     * Mark a note as relevant with a specific source
     *
     * @param noteId The ID of the note
     * @param source The source of relevance
     * @param triggeredAt When it became relevant
     */
    public void markAsRelevant(long noteId, RelevantSource source, Instant triggeredAt) {
        RelevantNoteEntry entry;

        switch (source) {
            case TIME_REMINDER:
                entry = RelevantNoteEntry.timeWindow(noteId, triggeredAt);
                break;
            case GEOFENCE_ENTER:
                entry = RelevantNoteEntry.geofence(noteId, triggeredAt);
                break;
            case GEOFENCE_EXIT:
                entry = RelevantNoteEntry.geofenceExit(noteId, triggeredAt);
                break;
            default:
                // Default to 24 hour expiration
                entry = RelevantNoteEntry.timeWindow(noteId, triggeredAt);
                entry.source = source;
                break;
        }

        insert(entry);
    }

    /**
     * Insert a relevant note entry
     *
     * @param entry The entry to insert
     */
    public void insert(RelevantNoteEntry entry) {
        if (db != null && db.relevantNoteDao() != null) {
            RelevantNoteEntity entity = convertToEntity(entry);
            db.relevantNoteDao().insert(entity);
        }
    }

    /**
     * Get all active (non-expired) relevant entries
     *
     * @return List of active relevant entries
     */
    public List<RelevantNoteEntry> getActiveEntries() {
        if (db == null || db.relevantNoteDao() == null) {
            return new ArrayList<>();
        }

        Instant now = Instant.now();
        long nowMillis = now.toEpochMilli();

        List<RelevantNoteEntity> entities = db.relevantNoteDao().getActiveEntries(nowMillis);
        return convertToEntryList(entities);
    }

    /**
     * Get all entries (including expired ones)
     *
     * @return List of all relevant entries
     */
    public List<RelevantNoteEntry> getAllEntries() {
        if (db == null || db.relevantNoteDao() == null) {
            return new ArrayList<>();
        }

        List<RelevantNoteEntity> entities = db.relevantNoteDao().getAllEntries();
        return convertToEntryList(entities);
    }

    /**
     * Get relevant entries for a specific note
     *
     * @param noteId The note ID
     * @return List of relevant entries for this note
     */
    public List<RelevantNoteEntry> getEntriesForNote(long noteId) {
        if (db == null || db.relevantNoteDao() == null) {
            return new ArrayList<>();
        }

        List<RelevantNoteEntity> entities = db.relevantNoteDao().getEntriesForNote(noteId);
        return convertToEntryList(entities);
    }

    /**
     * Check if a note is currently relevant
     *
     * @param noteId The note ID
     * @return true if the note has active relevant entries
     */
    public boolean isRelevant(long noteId) {
        if (db == null || db.relevantNoteDao() == null) {
            return false;
        }

        Instant now = Instant.now();
        long nowMillis = now.toEpochMilli();

        int count = db.relevantNoteDao().countActiveEntriesForNote(noteId, nowMillis);
        return count > 0;
    }

    /**
     * Remove a specific relevant entry
     *
     * @param entryId The entry ID to remove
     */
    public void remove(long entryId) {
        if (db != null && db.relevantNoteDao() != null) {
            db.relevantNoteDao().deleteById(entryId);
        }
    }

    /**
     * Remove all relevant entries for a specific note
     *
     * @param noteId The note ID
     */
    public void removeEntriesForNote(long noteId) {
        if (db != null && db.relevantNoteDao() != null) {
            db.relevantNoteDao().deleteByNoteId(noteId);
        }
    }

    /**
     * Expire (delete) old entries that are past their expiration time
     * Should be called periodically to clean up the database
     */
    public void expireOldEntries() {
        if (db == null || db.relevantNoteDao() == null) {
            return;
        }

        Instant now = Instant.now();
        long nowMillis = now.toEpochMilli();

        int deletedCount = db.relevantNoteDao().deleteExpiredEntries(nowMillis);

        // Log for debugging
        if (deletedCount > 0) {
            android.util.Log.d(TAG, "Expired " + deletedCount + " old relevant entries");
        }
    }

    /**
     * Clear all relevant entries
     */
    public void clearAll() {
        if (db != null && db.relevantNoteDao() != null) {
            db.relevantNoteDao().deleteAll();
        }
    }

    /**
     * Get count of active relevant entries
     *
     * @return Number of active entries
     */
    public int getActiveCount() {
        if (db == null || db.relevantNoteDao() == null) {
            return 0;
        }

        Instant now = Instant.now();
        long nowMillis = now.toEpochMilli();

        return db.relevantNoteDao().countActive(nowMillis);
    }

    // ========== CONVERSION HELPERS ==========

    /**
     * Convert RelevantNoteEntry to Room entity
     */
    private RelevantNoteEntity convertToEntity(RelevantNoteEntry entry) {
        RelevantNoteEntity entity = new RelevantNoteEntity();
        entity.id = entry.id;
        entity.noteId = entry.noteId;
        entity.source = entry.source != null ? entry.source.name() : null;
        entity.insertedAt = entry.insertedAt != null ? entry.insertedAt.toEpochMilli() : 0;
        entity.expiresAt = entry.expiresAt != null ? entry.expiresAt.toEpochMilli() : 0;
        return entity;
    }

    /**
     * Convert Room entity to RelevantNoteEntry
     */
    private RelevantNoteEntry convertFromEntity(RelevantNoteEntity entity) {
        RelevantNoteEntry entry = new RelevantNoteEntry();
        entry.id = entity.id;
        entry.noteId = entity.noteId;
        entry.source = entity.source != null ? RelevantSource.valueOf(entity.source) : null;
        entry.insertedAt = entity.insertedAt > 0 ? Instant.ofEpochMilli(entity.insertedAt) : null;
        entry.expiresAt = entity.expiresAt > 0 ? Instant.ofEpochMilli(entity.expiresAt) : null;
        return entry;
    }

    /**
     * Convert list of Room entities to list of RelevantNoteEntry
     */
    private List<RelevantNoteEntry> convertToEntryList(List<RelevantNoteEntity> entities) {
        List<RelevantNoteEntry> entries = new ArrayList<>();
        for (RelevantNoteEntity entity : entities) {
            entries.add(convertFromEntity(entity));
        }
        return entries;
    }
}