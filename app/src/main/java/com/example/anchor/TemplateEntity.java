package com.example.anchor;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

/**
 * TemplateEntity - Room database entity for templates
 */
@Entity(tableName = "templates")
public class TemplateEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "pageColorArgb")
    public int pageColorArgb;

    @ColumnInfo(name = "bodyHtml")
    public String bodyHtml;

    @ColumnInfo(name = "defaultChecklistJson")
    public String defaultChecklistJson; // JSON array of CheckListItem

    @ColumnInfo(name = "associatedTagIdsJson")
    public String associatedTagIdsJson; // JSON array of tag IDs

    @ColumnInfo(name = "isExample")
    public boolean isExample;

    /**
     * Default constructor (required by Room)
     */
    public TemplateEntity() {
    }
}
