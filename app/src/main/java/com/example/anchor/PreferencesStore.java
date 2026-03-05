package com.example.anchor;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * PreferencesStore - Manages app preferences and settings
 * Uses SharedPreferences for persistent storage
 */
public class PreferencesStore {

    private static final String PREFS_NAME = "AnchorPreferences";
    private SharedPreferences prefs;

    // Preference Keys
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_VIBRATION_ENABLED = "vibration_enabled";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_DEFAULT_REMINDER_TIME = "default_reminder_time";
    private static final String KEY_AUTO_EXPIRE_ENABLED = "auto_expire_enabled";
    private static final String KEY_EXPIRE_HOURS = "expire_hours";
    private static final String KEY_LOCATION_PERMISSION_REQUESTED = "location_permission_requested";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_LAST_SYNC_TIME = "last_sync_time";

    // Theme modes
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_SYSTEM = "system";

    /**
     * Constructor
     */
    public PreferencesStore(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ========== NOTIFICATIONS ==========

    /**
     * Check if notifications are enabled
     */
    public boolean areNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    /**
     * Enable or disable notifications
     */
    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }

    /**
     * Check if notification sound is enabled
     */
    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true);
    }

    /**
     * Enable or disable notification sound
     */
    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
    }

    /**
     * Check if vibration is enabled
     */
    public boolean isVibrationEnabled() {
        return prefs.getBoolean(KEY_VIBRATION_ENABLED, true);
    }

    /**
     * Enable or disable vibration
     */
    public void setVibrationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply();
    }

    // ========== THEME ==========

    /**
     * Get the current theme mode
     */
    public String getThemeMode() {
        return prefs.getString(KEY_THEME_MODE, THEME_SYSTEM);
    }

    /**
     * Set the theme mode
     */
    public void setThemeMode(String themeMode) {
        prefs.edit().putString(KEY_THEME_MODE, themeMode).apply();
    }

    /**
     * Check if dark mode is enabled
     */
    public boolean isDarkModeEnabled() {
        String theme = getThemeMode();
        if (theme.equals(THEME_DARK)) {
            return true;
        } else if (theme.equals(THEME_LIGHT)) {
            return false;
        } else {
            // System default - check system settings
            return false; // You can implement system theme detection
        }
    }

    // ========== REMINDERS ==========

    /**
     * Get default reminder time in hours
     */
    public int getDefaultReminderTime() {
        return prefs.getInt(KEY_DEFAULT_REMINDER_TIME, 1); // Default 1 hour
    }

    /**
     * Set default reminder time in hours
     */
    public void setDefaultReminderTime(int hours) {
        prefs.edit().putInt(KEY_DEFAULT_REMINDER_TIME, hours).apply();
    }

    /**
     * Check if auto-expire is enabled
     */
    public boolean isAutoExpireEnabled() {
        return prefs.getBoolean(KEY_AUTO_EXPIRE_ENABLED, true);
    }

    /**
     * Enable or disable auto-expire
     */
    public void setAutoExpireEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_EXPIRE_ENABLED, enabled).apply();
    }

    /**
     * Get expiration time in hours
     */
    public int getExpireHours() {
        return prefs.getInt(KEY_EXPIRE_HOURS, 24); // Default 24 hours
    }

    /**
     * Set expiration time in hours
     */
    public void setExpireHours(int hours) {
        prefs.edit().putInt(KEY_EXPIRE_HOURS, hours).apply();
    }

    // ========== PERMISSIONS ==========

    /**
     * Check if location permission was requested
     */
    public boolean wasLocationPermissionRequested() {
        return prefs.getBoolean(KEY_LOCATION_PERMISSION_REQUESTED, false);
    }

    /**
     * Mark that location permission was requested
     */
    public void setLocationPermissionRequested(boolean requested) {
        prefs.edit().putBoolean(KEY_LOCATION_PERMISSION_REQUESTED, requested).apply();
    }

    // ========== APP STATE ==========

    /**
     * Check if this is the first launch
     */
    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    /**
     * Mark that the app has been launched
     */
    public void setFirstLaunchComplete() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }

    /**
     * Get last sync time in milliseconds
     */
    public long getLastSyncTime() {
        return prefs.getLong(KEY_LAST_SYNC_TIME, 0);
    }

    /**
     * Set last sync time
     */
    public void setLastSyncTime(long timeMillis) {
        prefs.edit().putLong(KEY_LAST_SYNC_TIME, timeMillis).apply();
    }

    // ========== UTILITY METHODS ==========

    /**
     * Clear all preferences (reset to defaults)
     */
    public void clearAll() {
        prefs.edit().clear().apply();
    }

    /**
     * Export preferences as a string (for backup/debug)
     */
    public String exportPreferences() {
        StringBuilder sb = new StringBuilder();
        sb.append("Notifications: ").append(areNotificationsEnabled()).append("\n");
        sb.append("Sound: ").append(isSoundEnabled()).append("\n");
        sb.append("Vibration: ").append(isVibrationEnabled()).append("\n");
        sb.append("Theme: ").append(getThemeMode()).append("\n");
        sb.append("Default Reminder: ").append(getDefaultReminderTime()).append(" hours\n");
        sb.append("Auto Expire: ").append(isAutoExpireEnabled()).append("\n");
        sb.append("Expire Hours: ").append(getExpireHours()).append("\n");
        return sb.toString();
    }

    /**
     * Check if preferences have been initialized
     */
    public boolean hasPreferences() {
        return prefs.getAll().size() > 0;
    }
}