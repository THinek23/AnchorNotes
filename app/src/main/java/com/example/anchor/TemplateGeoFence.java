package com.example.anchor;

/**
 * TemplateGeoFence - Represents a geofence associated with a template
 * When entering/exiting this geofence, the template can be auto-applied
 */
public class TemplateGeoFence extends BaseEntity {

    public long templateId;
    public double centerLat;
    public double centerLon;
    public float radiusMeters;
    public String name; // Optional name for the geofence (e.g., "Home", "Office")

    /**
     * Default constructor
     */
    public TemplateGeoFence() {
        this.radiusMeters = 50.0f; // Default 50 meters
    }

    /**
     * Constructor with location
     */
    public TemplateGeoFence(double lat, double lon, float radiusMeters) {
        this.centerLat = lat;
        this.centerLon = lon;
        this.radiusMeters = radiusMeters;
    }

    /**
     * Constructor with location and name
     */
    public TemplateGeoFence(double lat, double lon, float radiusMeters, String name) {
        this(lat, lon, radiusMeters);
        this.name = name;
    }

    /**
     * Check if a point is within this geofence
     * @param lat Latitude to check
     * @param lon Longitude to check
     * @return true if point is within the geofence
     */
    public boolean contains(double lat, double lon) {
        float distance = distanceTo(lat, lon);
        return distance <= radiusMeters;
    }

    /**
     * Calculate distance from geofence center to a point
     * @param lat Target latitude
     * @param lon Target longitude
     * @return Distance in meters
     */
    public float distanceTo(double lat, double lon) {
        // Haversine formula
        final int EARTH_RADIUS = 6371000; // meters

        double dLat = Math.toRadians(lat - centerLat);
        double dLon = Math.toRadians(lon - centerLon);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(centerLat)) *
                        Math.cos(Math.toRadians(lat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (EARTH_RADIUS * c);
    }

    /**
     * Set the center location
     */
    public void setCenter(double lat, double lon) {
        this.centerLat = lat;
        this.centerLon = lon;
    }

    /**
     * Set the radius
     */
    public void setRadius(float radiusMeters) {
        if (radiusMeters > 0) {
            this.radiusMeters = radiusMeters;
        }
    }

    /**
     * Set the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get display name (name or coordinates)
     */
    public String getDisplayName() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return String.format("(%.4f, %.4f)", centerLat, centerLon);
    }

    @Override
    public String toString() {
        return "TemplateGeoFence{" +
                "id=" + id +
                ", templateId=" + templateId +
                ", name='" + name + '\'' +
                ", center=(" + centerLat + ", " + centerLon + ")" +
                ", radius=" + radiusMeters + "m" +
                '}';
    }
}