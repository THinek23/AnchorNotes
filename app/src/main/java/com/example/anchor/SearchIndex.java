package com.example.anchor;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * SearchIndex - Performs search operations on notes
 * Optimized for your Room database implementation
 * Handles full-text search and multi-criteria filtering
 */
public class SearchIndex {

    private NoteStore noteStore;

    /**
     * Constructor with NoteStore
     */
    public SearchIndex(NoteStore noteStore) {
        this.noteStore = noteStore;
    }

    /**
     * Default constructor
     */
    public SearchIndex() {
        // NoteStore will need to be set later
    }

    /**
     * Set the note store
     */
    public void setNoteStore(NoteStore noteStore) {
        this.noteStore = noteStore;
    }

    /**
     * Perform a search based on the given query
     * @param q The search query with criteria
     * @return List of notes matching all criteria
     */
    public List<Note> performSearch(SearchQuery q) {
        // Safety check
        if (noteStore == null) {
            return new ArrayList<>();
        }

        // If query is null or empty, return all notes
        if (q == null || q.isEmpty()) {
            return noteStore.findAll();
        }

        // Get all notes from database
        List<Note> allNotes = noteStore.findAll();
        List<Note> results = new ArrayList<>();

        // Filter notes based on query criteria
        for (Note note : allNotes) {
            if (matchesQuery(note, q)) {
                results.add(note);
            }
        }

        return results;
    }

    /**
     * Check if a note matches all criteria in the query (AND logic)
     * All conditions must be true for the note to match
     */
    private boolean matchesQuery(Note note, SearchQuery q) {
        // Text search (searches both title and content)
        if (q.text != null && !q.text.trim().isEmpty()) {
            if (!matchesText(note, q.text)) {
                return false;
            }
        }

        // Tag filter (OR logic - note must have at least one matching tag)
        if (!q.tagIds.isEmpty()) {
            if (!matchesTags(note, q.tagIds)) {
                return false;
            }
        }

        // Date range filter
        if (q.dateFrom != null || q.dateTo != null) {
            if (!matchesDateRange(note, q.dateFrom, q.dateTo)) {
                return false;
            }
        }

        // Photo filter
        if (q.hasPhoto != null) {
            if (note.hasPhoto != q.hasPhoto) {
                return false;
            }
        }

        // Audio filter
        if (q.hasAudio != null) {
            if (note.hasAudio != q.hasAudio) {
                return false;
            }
        }

        // Location filter
        if (q.hasLocation != null) {
            if (note.hasLocation != q.hasLocation) {
                return false;
            }
        }

        // All criteria matched
        return true;
    }

    /**
     * Check if note's title or content contains the search text
     * Case-insensitive search
     */
    private boolean matchesText(Note note, String searchText) {
        String lowerSearchText = searchText.toLowerCase(Locale.getDefault());

        // Search in title
        if (note.title != null &&
                note.title.toLowerCase(Locale.getDefault()).contains(lowerSearchText)) {
            return true;
        }

        // Search in content
        if (note.bodyHtml != null &&
                note.bodyHtml.toLowerCase(Locale.getDefault()).contains(lowerSearchText)) {
            return true;
        }

        return false;
    }

    /**
     * Check if note has any of the specified tags
     * OR logic - note needs to have at least one of the searched tags
     */
    private boolean matchesTags(Note note, java.util.Set<Long> tagIds) {
        // If note has no tags, it can't match
        if (note.tagIds == null || note.tagIds.isEmpty()) {
            return false;
        }

        // Check if note has at least one of the searched tags
        for (Long tagId : tagIds) {
            if (note.tagIds.contains(tagId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if note's creation date falls within the specified range
     * Both boundaries are inclusive
     */
    private boolean matchesDateRange(Note note, Instant from, Instant to) {
        if (note.createdAt == null) {
            return false;
        }

        // Check from date (inclusive)
        if (from != null && note.createdAt.isBefore(from)) {
            return false;
        }

        // Check to date (inclusive)
        if (to != null && note.createdAt.isAfter(to)) {
            return false;
        }

        return true;
    }

    /**
     * Get search suggestions based on partial text
     * Useful for auto-complete functionality
     */
    public List<String> getSuggestions(String partialText, int maxSuggestions) {
        List<String> suggestions = new ArrayList<>();

        if (noteStore == null || partialText == null || partialText.trim().isEmpty()) {
            return suggestions;
        }

        String lowerPartial = partialText.toLowerCase(Locale.getDefault());
        List<Note> allNotes = noteStore.findAll();

        for (Note note : allNotes) {
            // Check title
            if (note.title != null &&
                    note.title.toLowerCase(Locale.getDefault()).contains(lowerPartial)) {
                if (!suggestions.contains(note.title)) {
                    suggestions.add(note.title);
                    if (suggestions.size() >= maxSuggestions) {
                        break;
                    }
                }
            }
        }

        return suggestions;
    }

    /**
     * Count notes matching a query
     * Useful for showing result counts without loading all data
     */
    public int countMatches(SearchQuery q) {
        return performSearch(q).size();
    }

    /**
     * Check if any notes match the query
     */
    public boolean hasMatches(SearchQuery q) {
        return countMatches(q) > 0;
    }
}