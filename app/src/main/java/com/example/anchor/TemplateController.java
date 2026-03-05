package com.example.anchor;

import java.util.ArrayList;
import java.util.List;

/**
 * TemplateController - Manages template operations
 * Coordinates between UI and TemplateStore
 */
public class TemplateController {

    public TemplateStore templateStore;
    public List<Template> allTemplates;

    // Listener for changes
    private TemplateChangeListener listener;

    /**
     * Constructor
     */
    public TemplateController() {
        this.allTemplates = new ArrayList<>();
    }

    /**
     * Constructor with TemplateStore
     */
    public TemplateController(TemplateStore templateStore) {
        this.templateStore = templateStore;
        this.allTemplates = new ArrayList<>();
    }

    /**
     * Load all templates
     * @return List of all templates
     */
    public List<Template> loadAll() {
        if (templateStore != null) {
            allTemplates = templateStore.getAllTemplates();
        }
        return allTemplates;
    }

    /**
     * Load user templates (non-examples)
     */
    public List<Template> loadUserTemplates() {
        if (templateStore != null) {
            return templateStore.getUserTemplates();
        }
        return new ArrayList<>();
    }

    /**
     * Load example templates
     */
    public List<Template> loadExampleTemplates() {
        if (templateStore != null) {
            return templateStore.getExampleTemplates();
        }
        return new ArrayList<>();
    }

    /**
     * Create a new template
     * @param name Template name
     * @return The created template
     */
    public Template create(String name) {
        Template template = new Template(name);

        if (templateStore != null) {
            templateStore.save(template);
        }

        if (allTemplates != null) {
            allTemplates.add(template);
        }

        if (listener != null) {
            listener.onTemplateCreated(template);
        }

        return template;
    }

    /**
     * Create a template with body content
     */
    public Template create(String name, String bodyHtml) {
        Template template = new Template(name, bodyHtml);

        if (templateStore != null) {
            templateStore.save(template);
        }

        if (allTemplates != null) {
            allTemplates.add(template);
        }

        if (listener != null) {
            listener.onTemplateCreated(template);
        }

        return template;
    }

    /**
     * Save a template
     * @param template The template to save
     */
    public void save(Template template) {
        if (templateStore != null) {
            templateStore.save(template);

            // Update in allTemplates list
            if (allTemplates != null) {
                boolean found = false;
                for (int i = 0; i < allTemplates.size(); i++) {
                    if (allTemplates.get(i).id == template.id) {
                        allTemplates.set(i, template);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    allTemplates.add(template);
                }
            }

            if (listener != null) {
                listener.onTemplateUpdated(template);
            }
        }
    }

    /**
     * Delete a template by name
     * @param name The template name
     */
    public void delete(String name) {
        if (templateStore != null) {
            templateStore.delete(name);

            // Remove from allTemplates
            if (allTemplates != null) {
                allTemplates.removeIf(t -> name.equals(t.name));
            }

            if (listener != null) {
                listener.onTemplateDeleted(name);
            }
        }
    }

    /**
     * Delete a template by ID
     */
    public void delete(long id) {
        if (templateStore != null) {
            Template template = findById(id);
            String name = template != null ? template.name : null;

            templateStore.delete(id);

            // Remove from allTemplates
            if (allTemplates != null) {
                allTemplates.removeIf(t -> t.id == id);
            }

            if (listener != null && name != null) {
                listener.onTemplateDeleted(name);
            }
        }
    }

    /**
     * Find template by name
     */
    public Template findByName(String name) {
        if (templateStore != null) {
            return templateStore.findByName(name);
        }
        return null;
    }

    /**
     * Find template by ID
     */
    public Template findById(long id) {
        if (templateStore != null) {
            return templateStore.findById(id);
        }
        return null;
    }

    /**
     * Check if template exists
     */
    public boolean exists(String name) {
        if (templateStore != null) {
            return templateStore.exists(name);
        }
        return false;
    }

    /**
     * Get template count
     */
    public int getCount() {
        if (templateStore != null) {
            return templateStore.count();
        }
        return allTemplates != null ? allTemplates.size() : 0;
    }

    /**
     * Create a note from a template
     * @param templateId The template ID
     * @return A new note created from the template
     */
    public Note createNoteFromTemplate(long templateId) {
        Template template = findById(templateId);
        if (template != null) {
            Note note = template.createNote();

            if (listener != null) {
                listener.onNoteCreatedFromTemplate(template, note);
            }

            return note;
        }
        return null;
    }

    /**
     * Create a note from a template by name
     */
    public Note createNoteFromTemplate(String templateName) {
        Template template = findByName(templateName);
        if (template != null) {
            Note note = template.createNote();

            if (listener != null) {
                listener.onNoteCreatedFromTemplate(template, note);
            }

            return note;
        }
        return null;
    }

    /**
     * Add a geofence to a template
     */
    public void addGeofence(long templateId, TemplateGeoFence geofence) {
        Template template = findById(templateId);
        if (template != null) {
            template.addGeofence(geofence);
            save(template);
        }
    }

    /**
     * Remove a geofence from a template
     */
    public boolean removeGeofence(long templateId, long geofenceId) {
        Template template = findById(templateId);
        if (template != null) {
            boolean removed = template.removeGeofence(geofenceId);
            if (removed) {
                save(template);
            }
            return removed;
        }
        return false;
    }

    /**
     * Add a tag to a template
     */
    public void addTag(long templateId, long tagId) {
        Template template = findById(templateId);
        if (template != null) {
            template.addAssociatedTag(tagId);
            save(template);
        }
    }

    /**
     * Remove a tag from a template
     */
    public void removeTag(long templateId, long tagId) {
        Template template = findById(templateId);
        if (template != null) {
            template.removeAssociatedTag(tagId);
            save(template);
        }
    }

    /**
     * Add a checklist item to a template
     */
    public void addChecklistItem(long templateId, String itemText) {
        Template template = findById(templateId);
        if (template != null) {
            template.addChecklistItem(itemText);
            save(template);
        }
    }

    /**
     * Set the listener for template changes
     */
    public void setListener(TemplateChangeListener listener) {
        this.listener = listener;
    }

    /**
     * Refresh templates from store
     */
    public void refresh() {
        loadAll();
    }

    /**
     * Interface for listening to template changes
     */
    public interface TemplateChangeListener {
        void onTemplateCreated(Template template);
        void onTemplateUpdated(Template template);
        void onTemplateDeleted(String templateName);
        void onNoteCreatedFromTemplate(Template template, Note note);
    }
}