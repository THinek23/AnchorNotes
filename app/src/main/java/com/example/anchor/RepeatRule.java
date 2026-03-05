package com.example.anchor;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepeatRule {

    // Repeat type enum
    public enum RepeatType {
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY,
        RANDOM_DAYS
    }

    // Properties
    public RepeatType repeatType;
    public int interval; // e.g., every 2 days, every 3 weeks
    public List<DayOfWeek> daysOfWeek; // For weekly repeats
    public Integer dayOfMonth; // For monthly repeats (1-31)
    public Integer monthOfYear; // For yearly repeats (1-12)
    public Instant endDate; // Optional end date for the repeat rule
    public Integer maxOccurrences; // Optional max number of occurrences
    public int occurrenceCount; // Track how many times it has occurred
    public List<Instant> randomDates; // For random days scheduling

    // Constructor for daily repeat
    public RepeatRule(RepeatType repeatType, int interval) {
        this.repeatType = repeatType;
        this.interval = interval;
        this.occurrenceCount = 0;
        this.daysOfWeek = new ArrayList<>();
        this.randomDates = new ArrayList<>();
    }

    // Constructor for weekly repeat
    public RepeatRule(RepeatType repeatType, int interval, List<DayOfWeek> daysOfWeek) {
        this(repeatType, interval);
        this.daysOfWeek = new ArrayList<>(daysOfWeek);
    }

    // Constructor for monthly repeat
    public RepeatRule(RepeatType repeatType, int interval, int dayOfMonth) {
        this(repeatType, interval);
        this.dayOfMonth = dayOfMonth;
    }

    // Constructor for yearly repeat
    public RepeatRule(RepeatType repeatType, int interval, int monthOfYear, int dayOfMonth) {
        this(repeatType, interval);
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
    }

    // Constructor for random days
    public RepeatRule(List<Instant> randomDates) {
        this.repeatType = RepeatType.RANDOM_DAYS;
        this.interval = 0;
        this.occurrenceCount = 0;
        this.daysOfWeek = new ArrayList<>();
        this.randomDates = new ArrayList<>(randomDates);
        Collections.sort(this.randomDates);
    }


    public Instant calculateNextOccurrence(Instant currentTime, ZoneId zoneId) {
        // Check if we've reached max occurrences
        if (maxOccurrences != null && occurrenceCount >= maxOccurrences) {
            return null;
        }

        Instant nextOccurrence = null;

        switch (repeatType) {
            case DAILY:
                nextOccurrence = calculateNextDaily(currentTime);
                break;
            case WEEKLY:
                nextOccurrence = calculateNextWeekly(currentTime, zoneId);
                break;
            case MONTHLY:
                nextOccurrence = calculateNextMonthly(currentTime, zoneId);
                break;
            case YEARLY:
                nextOccurrence = calculateNextYearly(currentTime, zoneId);
                break;
            case RANDOM_DAYS:
                nextOccurrence = calculateNextRandomDay(currentTime);
                break;
        }

        // Check if next occurrence is past end date
        if (nextOccurrence != null && endDate != null && nextOccurrence.isAfter(endDate)) {
            return null;
        }

        return nextOccurrence;
    }

    private Instant calculateNextDaily(Instant currentTime) {
        return currentTime.plus(interval, ChronoUnit.DAYS);
    }

    private Instant calculateNextWeekly(Instant currentTime, ZoneId zoneId) {
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            // Default to weekly repeat on the same day
            return currentTime.plus(interval * 7L, ChronoUnit.DAYS);
        }

        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentTime, zoneId);
        DayOfWeek currentDayOfWeek = currentDateTime.getDayOfWeek();

        // Sort days of week
        List<DayOfWeek> sortedDays = new ArrayList<>(daysOfWeek);
        Collections.sort(sortedDays);

        // Find next day in the current week
        for (DayOfWeek day : sortedDays) {
            if (day.getValue() > currentDayOfWeek.getValue()) {
                long daysToAdd = day.getValue() - currentDayOfWeek.getValue();
                return currentDateTime.plusDays(daysToAdd)
                        .atZone(zoneId)
                        .toInstant();
            }
        }

        // No more days this week, go to next occurrence of first day
        DayOfWeek firstDay = sortedDays.get(0);
        long daysToAdd = (7 - currentDayOfWeek.getValue() + firstDay.getValue()) + ((interval - 1) * 7L);
        return currentDateTime.plusDays(daysToAdd)
                .atZone(zoneId)
                .toInstant();
    }

    private Instant calculateNextMonthly(Instant currentTime, ZoneId zoneId) {
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentTime, zoneId);
        LocalDateTime nextDateTime = currentDateTime.plusMonths(interval);

        if (dayOfMonth != null) {
            // Handle end of month edge cases
            int lastDayOfMonth = nextDateTime.getMonth().length(nextDateTime.toLocalDate().isLeapYear());
            int targetDay = Math.min(dayOfMonth, lastDayOfMonth);
            nextDateTime = nextDateTime.withDayOfMonth(targetDay);
        }

        return nextDateTime.atZone(zoneId).toInstant();
    }

    private Instant calculateNextYearly(Instant currentTime, ZoneId zoneId) {
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentTime, zoneId);
        LocalDateTime nextDateTime = currentDateTime.plusYears(interval);

        if (monthOfYear != null) {
            nextDateTime = nextDateTime.withMonth(monthOfYear);
        }

        if (dayOfMonth != null) {
            // Handle leap year and end of month edge cases
            int lastDayOfMonth = nextDateTime.getMonth().length(nextDateTime.toLocalDate().isLeapYear());
            int targetDay = Math.min(dayOfMonth, lastDayOfMonth);
            nextDateTime = nextDateTime.withDayOfMonth(targetDay);
        }

        return nextDateTime.atZone(zoneId).toInstant();
    }

    private Instant calculateNextRandomDay(Instant currentTime) {
        if (randomDates == null || randomDates.isEmpty()) {
            return null;
        }

        // Find the next date after current time
        for (Instant date : randomDates) {
            if (date.isAfter(currentTime)) {
                return date;
            }
        }

        // No more dates
        return null;
    }

    /**
     * Increment the occurrence count
     */
    public void incrementOccurrenceCount() {
        occurrenceCount++;
    }

    /**
     * Check if the repeat rule has more occurrences
     */
    public boolean hasMoreOccurrences(Instant currentTime, ZoneId zoneId) {
        if (maxOccurrences != null && occurrenceCount >= maxOccurrences) {
            return false;
        }

        if (endDate != null && currentTime.isAfter(endDate)) {
            return false;
        }

        if (repeatType == RepeatType.RANDOM_DAYS) {
            return calculateNextRandomDay(currentTime) != null;
        }

        return true;
    }

    /**
     * Set the end date for the repeat rule
     */
    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    /**
     * Set the maximum number of occurrences
     */
    public void setMaxOccurrences(int maxOccurrences) {
        this.maxOccurrences = maxOccurrences;
    }
}