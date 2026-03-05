package com.example.anchor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;
import java.util.List;

/**
 * TemplateDao - Room DAO for template operations
 */
@Dao
public interface TemplateDao {

    /**
     * Insert a template
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TemplateEntity template);

    /**
     * Update a template
     */
    @Update
    void update(TemplateEntity template);

    /**
     * Delete a template
     */
    @Delete
    void delete(TemplateEntity template);

    /**
     * Delete template by name
     */
    @Query("DELETE FROM templates WHERE name = :name")
    void deleteByName(String name);

    /**
     * Get all templates
     */
    @Query("SELECT * FROM templates ORDER BY name ASC")
    List<TemplateEntity> getAllTemplates();

    /**
     * Get template by ID
     */
    @Query("SELECT * FROM templates WHERE id = :id")
    TemplateEntity getById(long id);

    /**
     * Get template by name
     */
    @Query("SELECT * FROM templates WHERE name = :name")
    TemplateEntity getByName(String name);

    /**
     * Get example templates
     */
    @Query("SELECT * FROM templates WHERE isExample = 1")
    List<TemplateEntity> getExampleTemplates();

    /**
     * Get user templates (non-examples)
     */
    @Query("SELECT * FROM templates WHERE isExample = 0")
    List<TemplateEntity> getUserTemplates();

    /**
     * Delete all templates
     */
    @Query("DELETE FROM templates")
    void deleteAll();

    /**
     * Count templates
     */
    @Query("SELECT COUNT(*) FROM templates")
    int count();

    // ========== GEOFENCE OPERATIONS ==========

    /**
     * Insert a geofence
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertGeofence(TemplateGeoFenceEntity geofence);

    /**
     * Delete a geofence
     */
    @Delete
    void deleteGeofence(TemplateGeoFenceEntity geofence);

    /**
     * Delete geofence by ID
     */
    @Query("DELETE FROM template_geofences WHERE id = :id")
    void deleteGeofenceById(long id);

    /**
     * Get all geofences for a template
     */
    @Query("SELECT * FROM template_geofences WHERE templateId = :templateId")
    List<TemplateGeoFenceEntity> getGeofencesForTemplate(long templateId);

    /**
     * Delete all geofences for a template
     */
    @Query("DELETE FROM template_geofences WHERE templateId = :templateId")
    void deleteGeofencesForTemplate(long templateId);

    /**
     * Get all geofences
     */
    @Query("SELECT * FROM template_geofences")
    List<TemplateGeoFenceEntity> getAllGeofences();
}