package com.example.anchor;

import java.time.Instant;
import java.time.ZoneId;




public class TimeReminder extends Reminder {

    // Properties
    public Instant triggerAt;
    public RepeatRule repeatRule;

    public Instant lastFiredAt;

    // Constructor for one-time reminder
    public TimeReminder(long noteId, Instant scheduledTime) {
        this.noteId = noteId;
        this.triggerAt = scheduledTime;
        this.enabled = true;
        this.repeatRule = null;
    }

    // Constructor for repeating reminder
    public TimeReminder(long noteId, Instant scheduledTime, RepeatRule repeatRule) {
        this.noteId = noteId;
        this.triggerAt = scheduledTime;
        this.repeatRule = repeatRule;
        this.enabled = true;
    }


    /**
     * Check if this is a repeating reminder
     */
    public boolean isRepeating() {
        return repeatRule != null;
    }

    /**
     * Check if the reminder should fire at the given time
     */
    public boolean shouldFire(Instant currentTime) {
        if (!enabled) {
            return false;
        }

        // Check if scheduled time has passed
        return triggerAt != null &&
                (currentTime.equals(triggerAt) || currentTime.isAfter(triggerAt));
    }
    public void markFired(Instant when) {
        this.lastFiredAt = when;
        if (repeatRule != null) {
            ZoneId zoneId = java.time.ZoneId.systemDefault();
            repeatRule.incrementOccurrenceCount();
            if (!repeatRule.hasMoreOccurrences(triggerAt, zoneId)) {
                clearRepeatRule();
            } else {
                triggerAt = repeatRule.calculateNextOccurrence(triggerAt, zoneId);
            }

        }

    }

    /**
     * Update scheduled time to the next occurrence
     * Returns true if there is a next occurrence, false otherwise
     */
    public boolean updateToNextOccurrence(java.time.ZoneId zoneId) {
        if (repeatRule == null) {
            return false;
        }

        Instant nextOccurrence = repeatRule.calculateNextOccurrence(triggerAt, zoneId);

        if (nextOccurrence != null) {
            triggerAt = nextOccurrence;
            repeatRule.incrementOccurrenceCount();
            return true;
        }

        return false;
    }

    /**
     * Set the repeat rule for this reminder
     */
    public void setRepeatRule(RepeatRule repeatRule) {
        this.repeatRule = repeatRule;
    }

    /**
     * Clear the repeat rule (make it one-time)
     */
    public void clearRepeatRule() {
        this.repeatRule = null;
    }

    /**
     * Reschedule the reminder to a new time
     */
    public void reschedule(Instant newTime) {
        this.triggerAt = newTime;
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    /**
     * Get a description of the repeat pattern
     */
    public String getRepeatDescription() {
        if (repeatRule == null) {
            return "One-time";
        }

        switch (repeatRule.repeatType) {
            case DAILY:
                return repeatRule.interval == 1 ? "Daily" : "Every " + repeatRule.interval + " days";
            case WEEKLY:
                if (repeatRule.daysOfWeek.isEmpty()) {
                    return repeatRule.interval == 1 ? "Weekly" : "Every " + repeatRule.interval + " weeks";
                }
                return "Weekly on " + formatDaysOfWeek(repeatRule.daysOfWeek);
            case MONTHLY:
                return repeatRule.interval == 1 ? "Monthly" : "Every " + repeatRule.interval + " months";
            case YEARLY:
                return repeatRule.interval == 1 ? "Yearly" : "Every " + repeatRule.interval + " years";
            case RANDOM_DAYS:
                return "Random days (" + repeatRule.randomDates.size() + " dates)";
            default:
                return "Unknown";
        }
    }

    private String formatDaysOfWeek(java.util.List<java.time.DayOfWeek> days) {
        if (days.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
            sb.append(days.get(i).toString().substring(0, 3));
            if (i < days.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
