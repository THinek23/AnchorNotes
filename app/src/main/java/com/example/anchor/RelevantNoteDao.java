package com.example.anchor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;
import androidx.room.Delete;
import java.util.List;

/**
 * RelevantNoteDao - Room DAO for relevant notes operations
 */
@Dao
public interface RelevantNoteDao {

    /**
     * Insert a relevant note entry
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RelevantNoteEntity entry);

    /**
     * Insert multiple entries
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RelevantNoteEntity> entries);

    /**
     * Get all active (non-expired) relevant entries
     * @param currentTime Current time in milliseconds
     */
    @Query("SELECT * FROM relevant_notes WHERE expiresAt > :currentTime ORDER BY insertedAt DESC")
    List<RelevantNoteEntity> getActiveEntries(long currentTime);

    /**
     * Get all entries (including expired)
     */
    @Query("SELECT * FROM relevant_notes ORDER BY insertedAt DESC")
    List<RelevantNoteEntity> getAllEntries();

    /**
     * Get all relevant entries for a specific note
     */
    @Query("SELECT * FROM relevant_notes WHERE noteId = :noteId ORDER BY insertedAt DESC")
    List<RelevantNoteEntity> getEntriesForNote(long noteId);

    /**
     * Get active entries for a specific note
     */
    @Query("SELECT * FROM relevant_notes WHERE noteId = :noteId AND expiresAt > :currentTime")
    List<RelevantNoteEntity> getActiveEntriesForNote(long noteId, long currentTime);

    /**
     * Count active entries for a specific note
     */
    @Query("SELECT COUNT(*) FROM relevant_notes WHERE noteId = :noteId AND expiresAt > :currentTime")
    int countActiveEntriesForNote(long noteId, long currentTime);

    /**
     * Count all active entries
     */
    @Query("SELECT COUNT(*) FROM relevant_notes WHERE expiresAt > :currentTime")
    int countActive(long currentTime);

    /**
     * Delete expired entries
     * @param currentTime Current time in milliseconds
     * @return Number of deleted entries
     */
    @Query("DELETE FROM relevant_notes WHERE expiresAt <= :currentTime")
    int deleteExpiredEntries(long currentTime);

    /**
     * Delete a specific entry by ID
     */
    @Query("DELETE FROM relevant_notes WHERE id = :id")
    void deleteById(long id);

    /**
     * Delete all entries for a specific note
     */
    @Query("DELETE FROM relevant_notes WHERE noteId = :noteId")
    void deleteByNoteId(long noteId);

    /**
     * Delete all relevant entries
     */
    @Query("DELETE FROM relevant_notes")
    void deleteAll();

    /**
     * Get entry by ID
     */
    @Query("SELECT * FROM relevant_notes WHERE id = :id")
    RelevantNoteEntity getById(long id);

    /**
     * Get count of all entries
     */
    @Query("SELECT COUNT(*) FROM relevant_notes")
    int count();
}