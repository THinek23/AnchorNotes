package com.example.anchor;

import java.time.Instant;

public class GeofenceReminder extends Reminder {

    public double centerLat;

    public double centerLon;

    public float radiusMeters;

    public GeofenceTransition transitionType;

    public String geofenceId;

    public Instant activeSince;

    public Instant lastEnteredAt;

    public Instant lastExitedAt;

    public void onEnter(Instant when) {

    }

    public void onExit(Instant when) {

    }

}
