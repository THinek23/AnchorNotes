package com.example.anchor;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TemplateStore - Manages template storage with Room database
 * All methods fully implemented
 */
public class TemplateStore {

    private AppDatabase db;
    private Gson gson;

    /**
     * Constructor
     */
    public TemplateStore(Context context) {
        this.db = AppDatabase.getDatabase(context);
        this.gson = new Gson();
    }

    /**
     * Get all templates
     * @return List of all templates
     */
    public List<Template> getAllTemplates() {
        if (db == null || db.templateDao() == null) {
            return new ArrayList<>();
        }

        List<TemplateEntity> entities = db.templateDao().getAllTemplates();
        return convertToTemplateList(entities);
    }

    /**
     * Get user templates (non-examples)
     */
    public List<Template> getUserTemplates() {
        if (db == null || db.templateDao() == null) {
            return new ArrayList<>();
        }

        List<TemplateEntity> entities = db.templateDao().getUserTemplates();
        return convertToTemplateList(entities);
    }

    /**
     * Get example templates
     */
    public List<Template> getExampleTemplates() {
        if (db == null || db.templateDao() == null) {
            return new ArrayList<>();
        }

        List<TemplateEntity> entities = db.templateDao().getExampleTemplates();
        return convertToTemplateList(entities);
    }

    /**
     * Save a template (insert or update)
     * @param t The template to save
     */
    public void save(Template t) {
        if (db == null || db.templateDao() == null || t == null) {
            return;
        }

        TemplateEntity entity = convertToEntity(t);

        if (t.id == 0) {
            // Insert new template
            t.id = db.templateDao().insert(entity);
            entity.id = t.id;
        } else {
            // Update existing template
            db.templateDao().update(entity);
        }

        // Save geofences
        saveGeofences(t);
    }

    /**
     * Delete a template by name
     * @param name The template name to delete
     */
    public void delete(String name) {
        if (db == null || db.templateDao() == null || name == null) {
            return;
        }

        // Get template to get ID for deleting geofences
        TemplateEntity entity = db.templateDao().getByName(name);
        if (entity != null) {
            // Delete geofences first
            db.templateDao().deleteGeofencesForTemplate(entity.id);
            // Then delete template
            db.templateDao().deleteByName(name);
        }
    }

    /**
     * Delete a template by ID
     */
    public void delete(long id) {
        if (db == null || db.templateDao() == null) {
            return;
        }

        TemplateEntity entity = db.templateDao().getById(id);
        if (entity != null) {
            // Delete geofences first
            db.templateDao().deleteGeofencesForTemplate(id);
            // Then delete template
            db.templateDao().delete(entity);
        }
    }

    /**
     * Find template by name
     */
    public Template findByName(String name) {
        if (db == null || db.templateDao() == null || name == null) {
            return null;
        }

        TemplateEntity entity = db.templateDao().getByName(name);
        return entity != null ? convertFromEntity(entity) : null;
    }

    /**
     * Find template by ID
     */
    public Template findById(long id) {
        if (db == null || db.templateDao() == null) {
            return null;
        }

        TemplateEntity entity = db.templateDao().getById(id);
        return entity != null ? convertFromEntity(entity) : null;
    }

    /**
     * Check if template exists
     */
    public boolean exists(String name) {
        return findByName(name) != null;
    }

    /**
     * Get template count
     */
    public int count() {
        if (db == null || db.templateDao() == null) {
            return 0;
        }
        return db.templateDao().count();
    }

    /**
     * Delete all templates
     */
    public void deleteAll() {
        if (db != null && db.templateDao() != null) {
            db.templateDao().deleteAll();
        }
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Save geofences for a template
     */
    private void saveGeofences(Template template) {
        if (db == null || db.templateDao() == null || template == null) {
            return;
        }

        // Delete existing geofences
        db.templateDao().deleteGeofencesForTemplate(template.id);

        // Insert new geofences
        for (TemplateGeoFence geofence : template.associatedGeofences) {
            TemplateGeoFenceEntity entity = convertGeofenceToEntity(geofence);
            entity.templateId = template.id;
            long id = db.templateDao().insertGeofence(entity);
            geofence.id = id;
        }
    }

    /**
     * Load geofences for a template
     */
    private List<TemplateGeoFence> loadGeofences(long templateId) {
        if (db == null || db.templateDao() == null) {
            return new ArrayList<>();
        }

        List<TemplateGeoFenceEntity> entities = db.templateDao().getGeofencesForTemplate(templateId);
        List<TemplateGeoFence> geofences = new ArrayList<>();

        for (TemplateGeoFenceEntity entity : entities) {
            geofences.add(convertGeofenceFromEntity(entity));
        }

        return geofences;
    }

    /**
     * Convert Template to Room entity
     */
    private TemplateEntity convertToEntity(Template template) {
        TemplateEntity entity = new TemplateEntity();
        entity.id = template.id;
        entity.name = template.name;
        entity.pageColorArgb = template.pageColorArgb;
        entity.bodyHtml = template.bodyHtml;
        entity.isExample = template.isExample;

        // Convert checklist to JSON
        if (template.defaultChecklist != null && !template.defaultChecklist.isEmpty()) {
            entity.defaultChecklistJson = gson.toJson(template.defaultChecklist);
        }

        // Convert tag IDs to JSON
        if (template.associatedTagIds != null && !template.associatedTagIds.isEmpty()) {
            entity.associatedTagIdsJson = gson.toJson(template.associatedTagIds);
        }

        return entity;
    }

    /**
     * Convert Room entity to Template
     */
    private Template convertFromEntity(TemplateEntity entity) {
        Template template = new Template();
        template.id = entity.id;
        template.name = entity.name;
        template.pageColorArgb = entity.pageColorArgb;
        template.bodyHtml = entity.bodyHtml;
        template.isExample = entity.isExample;

        // Parse checklist from JSON
        if (entity.defaultChecklistJson != null && !entity.defaultChecklistJson.isEmpty()) {
            Type listType = new TypeToken<ArrayList<CheckListItem>>(){}.getType();
            template.defaultChecklist = gson.fromJson(entity.defaultChecklistJson, listType);
        }

        // Parse tag IDs from JSON
        if (entity.associatedTagIdsJson != null && !entity.associatedTagIdsJson.isEmpty()) {
            Type setType = new TypeToken<HashSet<Long>>(){}.getType();
            template.associatedTagIds = gson.fromJson(entity.associatedTagIdsJson, setType);
        }

        // Load geofences
        template.associatedGeofences = loadGeofences(entity.id);

        return template;
    }

    /**
     * Convert list of entities to list of templates
     */
    private List<Template> convertToTemplateList(List<TemplateEntity> entities) {
        List<Template> templates = new ArrayList<>();
        for (TemplateEntity entity : entities) {
            templates.add(convertFromEntity(entity));
        }
        return templates;
    }

    /**
     * Convert TemplateGeoFence to entity
     */
    private TemplateGeoFenceEntity convertGeofenceToEntity(TemplateGeoFence geofence) {
        TemplateGeoFenceEntity entity = new TemplateGeoFenceEntity();
        entity.id = geofence.id;
        entity.templateId = geofence.templateId;
        entity.centerLat = geofence.centerLat;
        entity.centerLon = geofence.centerLon;
        entity.radiusMeters = geofence.radiusMeters;
        entity.name = geofence.name;
        return entity;
    }

    /**
     * Convert entity to TemplateGeoFence
     */
    private TemplateGeoFence convertGeofenceFromEntity(TemplateGeoFenceEntity entity) {
        TemplateGeoFence geofence = new TemplateGeoFence();
        geofence.id = entity.id;
        geofence.templateId = entity.templateId;
        geofence.centerLat = entity.centerLat;
        geofence.centerLon = entity.centerLon;
        geofence.radiusMeters = entity.radiusMeters;
        geofence.name = entity.name;
        return geofence;
    }
}