package com.example.anchor;

import java.time.Instant;

/**
 * CheckListItem - Represents an item in a checklist
 * Can be checked/unchecked and has ordering
 */
public class CheckListItem extends BaseEntity {

    public String uuid;
    public String text;
    public boolean checked;
    public int orderIndex;
    public Instant recordedAt;

    /**
     * Default constructor
     */
    public CheckListItem() {
        this.checked = false;
        this.recordedAt = Instant.now();
        this.uuid = java.util.UUID.randomUUID().toString();
    }

    /**
     * Constructor with text
     */
    public CheckListItem(String text) {
        this();
        this.text = text;
    }

    /**
     * Constructor with text and order
     */
    public CheckListItem(String text, int orderIndex) {
        this(text);
        this.orderIndex = orderIndex;
    }

    /**
     * Set the text of this checklist item
     * @param t The new text
     */
    public void setText(String t) {
        if (t != null && !t.trim().isEmpty()) {
            this.text = t.trim();
            this.recordedAt = Instant.now();
        }
    }

    /**
     * Toggle the checked state of this item
     */
    public void toggle() {
        this.checked = !this.checked;
        this.recordedAt = Instant.now();
    }

    /**
     * Check this item
     */
    public void check() {
        if (!this.checked) {
            this.checked = true;
            this.recordedAt = Instant.now();
        }
    }

    /**
     * Uncheck this item
     */
    public void uncheck() {
        if (this.checked) {
            this.checked = false;
            this.recordedAt = Instant.now();
        }
    }

    /**
     * Check if this item is checked
     */
    public boolean isChecked() {
        return this.checked;
    }

    /**
     * Check if this item is empty (no text)
     */
    public boolean isEmpty() {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Create a copy of this checklist item
     */
    public CheckListItem copy() {
        CheckListItem copy = new CheckListItem();
        copy.text = this.text;
        copy.checked = this.checked;
        copy.orderIndex = this.orderIndex;
        copy.uuid = java.util.UUID.randomUUID().toString(); // New UUID for copy
        return copy;
    }

    @Override
    public String toString() {
        return "CheckListItem{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", text='" + text + '\'' +
                ", checked=" + checked +
                ", orderIndex=" + orderIndex +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CheckListItem that = (CheckListItem) obj;
        return uuid != null ? uuid.equals(that.uuid) : super.equals(obj);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : super.hashCode();
    }
}