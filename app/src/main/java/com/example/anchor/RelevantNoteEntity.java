package com.example.anchor;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

/**
 * RelevantNoteEntity - Room database entity for relevant notes
 * Stores which notes are currently relevant and why
 */
@Entity(
        tableName = "relevant_notes",
        indices = {
                @Index(value = "noteId"),
                @Index(value = "expiresAt")
        }
)
public class RelevantNoteEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "noteId")
    public long noteId;

    @ColumnInfo(name = "source")
    public String source; // Stored as string (enum name)

    @ColumnInfo(name = "insertedAt")
    public long insertedAt; // Timestamp in milliseconds

    @ColumnInfo(name = "expiresAt")
    public long expiresAt; // Timestamp in milliseconds

    /**
     * Default constructor (required by Room)
     */
    public RelevantNoteEntity() {
    }

    /**
     * Constructor with all fields
     */
    public RelevantNoteEntity(long noteId, String source, long insertedAt, long expiresAt) {
        this.noteId = noteId;
        this.source = source;
        this.insertedAt = insertedAt;
        this.expiresAt = expiresAt;
    }
}