package com.example.anchor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * RelevantNotesController - Manages relevant notes
 * Coordinates between UI and RelevantStore
 */
public class RelevantNotesController {

    // Properties
    public RelevantStore relevantStore;
    public List<RelevantNoteEntry> entries;

    // Listener for changes
    private RelevantNotesListener listener;

    /**
     * Constructor
     */
    public RelevantNotesController() {
        this.entries = new ArrayList<>();
    }

    /**
     * Constructor with RelevantStore
     */
    public RelevantNotesController(RelevantStore relevantStore) {
        this.relevantStore = relevantStore;
        this.entries = new ArrayList<>();
    }

    /**
     * Load all active relevant entries
     *
     * @return List of active relevant entries
     */
    public List<RelevantNoteEntry> loadActive() {
        if (relevantStore != null) {
            entries = relevantStore.getActiveEntries();
        } else {
            entries = new ArrayList<>();
        }

        // Notify listener
        if (listener != null) {
            listener.onRelevantNotesLoaded(entries);
        }

        return entries;
    }

    /**
     * Get all entries (cached)
     *
     * @return Cached list of entries
     */
    public List<RelevantNoteEntry> getEntries() {
        return entries != null ? entries : new ArrayList<>();
    }

    /**
     * Get count of active relevant notes
     *
     * @return Number of active relevant notes
     */
    public int getActiveCount() {
        if (relevantStore != null) {
            return relevantStore.getActiveCount();
        }
        return entries != null ? entries.size() : 0;
    }

    /**
     * Check if there are any active relevant notes
     *
     * @return true if there are active relevant notes
     */
    public boolean hasActiveNotes() {
        return getActiveCount() > 0;
    }

    /**
     * Mark a note as relevant with time-based trigger
     *
     * @param noteId The note ID
     * @param firedAt When it became relevant
     */
    public void markAsRelevant(long noteId, Instant firedAt) {
        if (relevantStore != null) {
            relevantStore.markAsRelevant(noteId, firedAt);
            loadActive(); // Refresh the list
        }
    }

    /**
     * Mark a note as relevant with specific source
     *
     * @param noteId The note ID
     * @param source The source of relevance
     * @param triggeredAt When it became relevant
     */
    public void markAsRelevant(long noteId, RelevantSource source, Instant triggeredAt) {
        if (relevantStore != null) {
            relevantStore.markAsRelevant(noteId, source, triggeredAt);
            loadActive(); // Refresh the list
        }
    }

    /**
     * Remove a relevant entry
     *
     * @param entryId The entry ID to remove
     */
    public void removeEntry(long entryId) {
        if (relevantStore != null) {
            relevantStore.remove(entryId);
            loadActive(); // Refresh the list

            if (listener != null) {
                listener.onEntryRemoved(entryId);
            }
        }
    }

    /**
     * Remove all relevant entries for a note
     *
     * @param noteId The note ID
     */
    public void removeEntriesForNote(long noteId) {
        if (relevantStore != null) {
            relevantStore.removeEntriesForNote(noteId);
            loadActive(); // Refresh the list

            if (listener != null) {
                listener.onNoteEntriesCleared(noteId);
            }
        }
    }

    /**
     * Dismiss a relevant note (remove its entries and update UI)
     *
     * @param noteId The note ID to dismiss
     */
    public void dismissNote(long noteId) {
        removeEntriesForNote(noteId);
    }

    /**
     * Expire old entries
     * Should be called periodically (e.g., on app start, every few hours)
     */
    public void expireOldEntries() {
        if (relevantStore != null) {
            relevantStore.expireOldEntries();
            loadActive(); // Refresh after cleanup
        }
    }

    /**
     * Clear all relevant entries
     */
    public void clearAll() {
        if (relevantStore != null) {
            relevantStore.clearAll();
            entries = new ArrayList<>();

            if (listener != null) {
                listener.onAllCleared();
            }
        }
    }

    /**
     * Get relevant entries for a specific note
     *
     * @param noteId The note ID
     * @return List of relevant entries for this note
     */
    public List<RelevantNoteEntry> getEntriesForNote(long noteId) {
        if (relevantStore != null) {
            return relevantStore.getEntriesForNote(noteId);
        }
        return new ArrayList<>();
    }

    /**
     * Check if a note is currently relevant
     *
     * @param noteId The note ID
     * @return true if the note is relevant
     */
    public boolean isNoteRelevant(long noteId) {
        if (relevantStore != null) {
            return relevantStore.isRelevant(noteId);
        }
        return false;
    }

    /**
     * Get unique note IDs from active entries
     * Useful for displaying a list of relevant notes without duplicates
     *
     * @return List of unique note IDs
     */
    public List<Long> getUniqueNoteIds() {
        List<Long> noteIds = new ArrayList<>();
        if (entries != null) {
            for (RelevantNoteEntry entry : entries) {
                if (!noteIds.contains(entry.noteId)) {
                    noteIds.add(entry.noteId);
                }
            }
        }
        return noteIds;
    }

    /**
     * Set a listener for relevant notes changes
     *
     * @param listener The listener
     */
    public void setListener(RelevantNotesListener listener) {
        this.listener = listener;
    }

    /**
     * Refresh the relevant notes list
     * Call this when returning to the app or after data changes
     */
    public void refresh() {
        loadActive();
    }

    /**
     * Interface for listening to relevant notes changes
     */
    public interface RelevantNotesListener {
        /**
         * Called when relevant notes are loaded
         */
        void onRelevantNotesLoaded(List<RelevantNoteEntry> entries);

        /**
         * Called when an entry is removed
         */
        void onEntryRemoved(long entryId);

        /**
         * Called when all entries for a note are cleared
         */
        void onNoteEntriesCleared(long noteId);

        /**
         * Called when all entries are cleared
         */
        void onAllCleared();
    }
}