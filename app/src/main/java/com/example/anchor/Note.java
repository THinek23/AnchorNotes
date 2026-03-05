package com.example.anchor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Note extends BaseEntity {

    // Properties
    public String title;
    public String bodyHtml;
    public List<CheckListItem> checklist = new ArrayList<>();
    public List<Attachment> attachments = new ArrayList<>();
    public boolean pinned;
    public GeoTag location;
    public Set<Long> tagIds = new HashSet<>();
    public boolean hasPhoto;
    public boolean hasLocation;
    public Long templateId;
    public boolean hasAudio;
    public TimeReminder timeReminder;
    public GeofenceReminder geoFenceReminder;

    // Methods
    public void rename(String newTitle) {
        this.title = newTitle;
    }

    public void setBodyHTML(String html) {
        this.bodyHtml = html;
    }

    public void togglePinned() {
        this.pinned = !this.pinned;
    }

    public void addTag(long tagID) {
        tagIds.add(tagID);
    }

    public void removeTag(long tagID) {
        tagIds.remove(tagID);
    }

    public void clearTags() {
        tagIds.clear();
    }

    public CheckListItem addChecklistItem(String uuid, String text, boolean checked, int orderIndex) {
        CheckListItem item = new CheckListItem();
        item.uuid = uuid;
        item.text = text;
        item.checked = checked;
        item.orderIndex = orderIndex;
        checklist.add(item);
        return item;
    }

    public boolean removeChecklistItem(String uuid) {
        return checklist.removeIf(item -> item.uuid.equals(uuid));
    }

    public void toggleChecklistItem(String uuid) {
        for (CheckListItem item : checklist) {
            if (item.uuid.equals(uuid)) {
                item.checked = !item.checked;
                break;
            }
        }
    }

    public void reorderChecklist(List<String> uuidsInOrder) {
        List<CheckListItem> reordered = new ArrayList<>();
        for (String uuid : uuidsInOrder) {
            for (CheckListItem item : checklist) {
                if (item.uuid.equals(uuid)) {
                    reordered.add(item);
                    break;
                }
            }
        }
        checklist.clear();
        checklist.addAll(reordered);
    }

    public int countUnchecked() {
        int count = 0;
        for (CheckListItem item : checklist) {
            if (!item.checked) {
                count++;
            }
        }
        return count;
    }

    public Attachment attach(Attachment a) {
        // TODO what is the purpose of returning attachment, should it return null in some senarios?
        attachments.add(a);
        return a;
    }

    public boolean detachById(long attachmentId) {
        return attachments.removeIf(attachment -> attachment.id == attachmentId);
    }

    public void setLocation(GeoTag geo) {
        this.location = geo;
    }

    public void removeLocation() {
        this.location = null;
    }

    public void applyTemplate(Template t) {
       // TODO
    }

    public void setTimeReminder(TimeReminder r) {
        this.timeReminder = r;
    }

    public void clearTimeReminder() {
        this.timeReminder = null;
    }

    public void setGeofenceReminder(GeofenceReminder r) {
        this.geoFenceReminder = r;
    }

    public void clearGeofenceReminder() {
        this.geoFenceReminder = null;
    }

    public void recomputeMediaFlags() {
        this.hasPhoto = false;
        this.hasAudio = false;
        this.hasLocation = (location != null);

        for (Attachment attachment : attachments) {
            if (attachment.isPhoto()) {
                this.hasPhoto = true;
            }
            if (attachment.isAudio()) {
                this.hasAudio = true;
            }
        }
    }
}