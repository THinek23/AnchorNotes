package com.example.anchor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;
import androidx.room.Delete;
import androidx.room.Update;
import java.util.List;

// NotesDao - Complete Room DAO with all required queries

@Dao
public interface NotesDao {



    // Get a single note by ID
    @Query("SELECT * FROM notes WHERE nid = :nid")
    Notes getNoteById(long nid);


     // Get all notes (required for search)
    @Query("SELECT * FROM notes ORDER BY nid DESC")
    List<Notes> getAllNotes();

    // Get count of all notes
    @Query("SELECT COUNT(*) FROM notes")
    int getNotesCount();

    // Insert a new note
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Notes note);


     // Insert multiple notes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotes(List<Notes> notes);


    // Update an existing note
    @Update
    void updateNote(Notes note);

    // Delete a note
    @Delete
    void deleteNote(Notes note);



    // Delete a note by ID

    @Query("DELETE FROM notes WHERE nid = :nid")
    void deleteNoteById(long nid);


    // Delete all notes

    @Query("DELETE FROM notes")
    void deleteAllNotes();

    //  Search notes by title or content (case-insensitive)

    @Query("SELECT * FROM notes WHERE Title LIKE '%' || :searchText || '%' OR Content LIKE '%' || :searchText || '%' ORDER BY nid DESC")
    List<Notes> searchNotes(String searchText);


    // Get recent notes (limit)
    @Query("SELECT * FROM notes ORDER BY nid DESC LIMIT :limit")
    List<Notes> getRecentNotes(int limit);
}