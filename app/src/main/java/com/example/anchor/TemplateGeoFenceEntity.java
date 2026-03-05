package com.example.anchor;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * TemplateGeoFenceEntity - Room database entity for template geofences
 */
@Entity(
        tableName = "template_geofences",
        indices = {@Index(value = "templateId")}
)
public class TemplateGeoFenceEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "templateId")
    public long templateId;

    @ColumnInfo(name = "centerLat")
    public double centerLat;

    @ColumnInfo(name = "centerLon")
    public double centerLon;

    @ColumnInfo(name = "radiusMeters")
    public float radiusMeters;

    @ColumnInfo(name = "name")
    public String name;

    /**
     * Default constructor (required by Room)
     */
    public TemplateGeoFenceEntity() {
    }
}