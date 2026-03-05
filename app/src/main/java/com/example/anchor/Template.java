package com.example.anchor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Template - Represents a note template
 * Templates can have default content, checklists, tags, and geofences
 */
public class Template extends BaseEntity {

    public String name;
    public int pageColorArgb;
    public String bodyHtml;
    public List<CheckListItem> defaultChecklist = new ArrayList<>();
    public Set<Long> associatedTagIds = new HashSet<>();
    public List<TemplateGeoFence> associatedGeofences = new ArrayList<>();
    public boolean isExample;

    /**
     * Default constructor
     */
    public Template() {
        this.defaultChecklist = new ArrayList<>();
        this.associatedTagIds = new HashSet<>();
        this.associatedGeofences = new ArrayList<>();
        this.isExample = false;
    }

    /**
     * Constructor with name
     */
    public Template(String name) {
        this();
        this.name = name;
    }

    /**
     * Constructor with name and body
     */
    public Template(String name, String bodyHtml) {
        this(name);
        this.bodyHtml = bodyHtml;
    }

    /**
     * Add a geofence to this template
     * @param g The geofence to add
     */
    public void addGeofence(TemplateGeoFence g) {
        if (g != null) {
            g.templateId = this.id;

            // Check if already exists (by ID)
            boolean exists = false;
            for (TemplateGeoFence existing : associatedGeofences) {
                if (existing.id == g.id && g.id > 0) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                associatedGeofences.add(g);
            }
        }
    }

    /**
     * Remove a geofence by ID
     * @param id The geofence ID to remove
     * @return true if removed, false if not found
     */
    public boolean removeGeofence(long id) {
        return associatedGeofences.removeIf(g -> g.id == id);
    }

    /**
     * Remove all geofences
     */
    public void clearGeofences() {
        associatedGeofences.clear();
    }

    /**
     * Get geofence by ID
     */
    public TemplateGeoFence getGeofence(long id) {
        for (TemplateGeoFence g : associatedGeofences) {
            if (g.id == id) {
                return g;
            }
        }
        return null;
    }

    /**
     * Add an associated tag
     * @param tagId The tag ID to associate
     */
    public void addAssociatedTag(long tagId) {
        associatedTagIds.add(tagId);
    }

    /**
     * Remove an associated tag
     * @param tagId The tag ID to remove
     */
    public void removeAssociatedTag(long tagId) {
        associatedTagIds.remove(tagId);
    }

    /**
     * Check if a tag is associated
     */
    public boolean hasTag(long tagId) {
        return associatedTagIds.contains(tagId);
    }

    /**
     * Clear all associated tags
     */
    public void clearTags() {
        associatedTagIds.clear();
    }

    /**
     * Add a checklist item
     * @param item The checklist item to add
     */
    public void addChecklistItem(CheckListItem item) {
        if (item != null) {
            item.orderIndex = defaultChecklist.size();
            defaultChecklist.add(item);
        }
    }

    /**
     * Add a checklist item by text
     * @param text The item text
     */
    public void addChecklistItem(String text) {
        CheckListItem item = new CheckListItem(text, defaultChecklist.size());
        defaultChecklist.add(item);
    }

    /**
     * Remove a checklist item
     * @param item The item to remove
     * @return true if removed
     */
    public boolean removeChecklistItem(CheckListItem item) {
        boolean removed = defaultChecklist.remove(item);
        if (removed) {
            reorderChecklist();
        }
        return removed;
    }

    /**
     * Remove checklist item by index
     * @param index The index to remove
     * @return true if removed
     */
    public boolean removeChecklistItem(int index) {
        if (index >= 0 && index < defaultChecklist.size()) {
            defaultChecklist.remove(index);
            reorderChecklist();
            return true;
        }
        return false;
    }

    /**
     * Reorder checklist items after removal
     */
    private void reorderChecklist() {
        for (int i = 0; i < defaultChecklist.size(); i++) {
            defaultChecklist.get(i).orderIndex = i;
        }
    }

    /**
     * Clear all checklist items
     */
    public void clearChecklist() {
        defaultChecklist.clear();
    }

    /**
     * Get a copy of the checklist (for creating notes)
     */
    public List<CheckListItem> getChecklistCopy() {
        List<CheckListItem> copy = new ArrayList<>();
        for (CheckListItem item : defaultChecklist) {
            copy.add(item.copy());
        }
        return copy;
    }

    /**
     * Set the page color
     * @param argb The color in ARGB format
     */
    public void setPageColor(int argb) {
        this.pageColorArgb = argb;
    }

    /**
     * Check if template has a color set
     */
    public boolean hasColor() {
        return pageColorArgb != 0;
    }

    /**
     * Check if template has body content
     */
    public boolean hasBodyContent() {
        return bodyHtml != null && !bodyHtml.trim().isEmpty();
    }

    /**
     * Check if template has checklist items
     */
    public boolean hasChecklist() {
        return !defaultChecklist.isEmpty();
    }

    /**
     * Check if template has geofences
     */
    public boolean hasGeofences() {
        return !associatedGeofences.isEmpty();
    }

    /**
     * Check if template has associated tags
     */
    public boolean hasTags() {
        return !associatedTagIds.isEmpty();
    }

    /**
     * Mark this as an example template
     */
    public void markAsExample() {
        this.isExample = true;
    }

    /**
     * Create a note from this template
     * Returns a Note with all template data applied
     */
    public Note createNote() {
        Note note = new Note();
        note.title = this.name;
        note.bodyHtml = this.bodyHtml;

        // Copy tags
        if (!associatedTagIds.isEmpty()) {
            note.tagIds = new HashSet<>(associatedTagIds);
        }

        // Note: Checklist would need to be handled separately
        // as Note doesn't have a checklist field yet

        return note;
    }

    @Override
    public String toString() {
        return "Template{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", hasBody=" + hasBodyContent() +
                ", checklistItems=" + defaultChecklist.size() +
                ", tags=" + associatedTagIds.size() +
                ", geofences=" + associatedGeofences.size() +
                ", isExample=" + isExample +
                '}';
    }
}