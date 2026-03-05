package com.example.anchor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class SearchQuery {
    public String text;

    public Set<Long> tagIds = new HashSet<>();

    public Instant dateFrom;

    public Instant dateTo;

    public Boolean hasPhoto;

    public Boolean hasAudio;

    public Boolean hasLocation;

    // Search for notes containing this text in title or content
    public SearchQuery withText(String t) {
        this.text = t;
        return this;
    }

    // Filter notes that have this tag
    public SearchQuery withTag(long id) {
        this.tagIds.add(id);
        return this;
    }
    // Filter notes within a date range
    public SearchQuery within(Instant from, Instant to) {
        this.dateFrom = from;
        this.dateTo = to;
        return this;
    }
    // Filter notes that have (or don't have) photos
    public SearchQuery requirePhoto(Boolean v) {
        this.hasPhoto = v;
        return this;

    }

    // Filter notes that have (or don't have) audio
    public SearchQuery requireAudio(Boolean v) {
        this.hasAudio = v;
        return this;
    }

    // Filter notes that have (or don't have) location data
    public SearchQuery requireLocation(Boolean v) {
        this.hasLocation = v;
        return this;
    }

    // Check if this query is empty (has no criteria)
    public boolean isEmpty() {
        return (text == null || text.trim().isEmpty()) &&
                tagIds.isEmpty() &&
                dateFrom == null &&
                dateTo == null &&
                hasPhoto == null &&
                hasAudio == null &&
                hasLocation == null;
    }

    // Reset all search criteria
    public void clear() {
        text = null;
        tagIds.clear();
        dateFrom = null;
        dateTo = null;
        hasPhoto = null;
        hasAudio = null;
        hasLocation = null;
    }

}
