package com.example.anchor;

import java.util.List;

public class HomeController {

    // Properties
    public List<Note> pinned;
    public List<Note> recent;
    public List<Note> relevantNow;
    public NoteStore noteStore;
    public RelevantStore relevantStore;
    public PreferencesStore preferencesStore;

    // Methods
    public void refreshHome() {
        if (noteStore != null) {
            // Load recent notes
            recent = noteStore.getRecentNotes();
        }

        if (relevantStore != null) {
            // Load currently relevant notes
            List<RelevantNoteEntry> relevantEntries = relevantStore.getActiveEntries();

            if (relevantEntries != null && noteStore != null) {
                relevantNow = noteStore.getNotesByIds(
                        relevantEntries.stream()
                                .map(entry -> entry.noteId)
                                .toList()
                );
            }
        }
    }

    public void expireTimeBased() {
        if (relevantStore != null) {
            relevantStore.expireOldEntries();

            // Refresh the relevant notes list
            refreshHome();
        }
    }
}
