package com.example.anchor;

import java.util.HashSet;
import java.util.Set;

public class UserPreferences {
    public float defaultGeofenceRadiusMeters = 804.672f;

    public SortOrder sortOrder = SortOrder.UPDATED_DESC;

    public Set<Long> tagIds = new HashSet<>();

    public boolean showPinnedFirst = true;

    public boolean askUpdateLocationOnEdit = true;

    public boolean autoReverseGeocode = false;

    public void setDefaultRadiusMeters(float m) {
        // TODO
    }

    public void setSortOrder(SortOrder o) {
        // TODO
    }

    public void setShowPinnedFirst(boolean v) {
        // TODO
    }

    public void setAutoReverseGeocode(boolean v) {
        // TODO
    }

}
