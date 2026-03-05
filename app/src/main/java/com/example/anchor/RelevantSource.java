package com.example.anchor;

/**
 * RelevantSource - Enum for tracking the source of relevant notes
 * Indicates why a note became relevant
 */
public enum RelevantSource {
    /**
     * Note became relevant due to a time-based reminder firing
     */
    TIME_REMINDER("Time Reminder"),

    /**
     * Note became relevant due to entering a geofence
     */
    GEOFENCE_ENTER("Geofence Enter"),

    /**
     * Note became relevant due to exiting a geofence
     */
    GEOFENCE_EXIT("Geofence Exit"),

    /**
     * Note was manually marked as relevant by user
     */
    MANUAL("Manual"),

    /**
     * Note became relevant due to proximity to a location
     */
    LOCATION_PROXIMITY("Location Proximity");

    private final String displayName;

    RelevantSource(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this source is location-based
     */
    public boolean isLocationBased() {
        return this == GEOFENCE_ENTER ||
                this == GEOFENCE_EXIT ||
                this == LOCATION_PROXIMITY;
    }

    /**
     * Check if this source is time-based
     */
    public boolean isTimeBased() {
        return this == TIME_REMINDER;
    }
}