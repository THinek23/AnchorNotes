package com.example.anchor;

import java.util.List;

public class TagController {

    // Properties
    public TagStore tagStore;
    public List<Tag> allTags;

    // Methods
    public List<Tag> loadAll() {
        if (tagStore != null) {
            allTags = tagStore.getAllTags();
        }
        return allTags;
    }

    public Tag create(String name, Integer color) {
        Tag tag = new Tag();
        tag.name = name;
        tag.color = color;

        if (tagStore != null) {
            tagStore.save(tag);
        }

        if (allTags != null) {
            allTags.add(tag);
        }

        return tag;
    }

    public void rename(long id, String newName) {
        if (tagStore != null) {
            Tag tag = tagStore.findById(id);
            if (tag != null) {
                tag.name = newName;
                tagStore.save(tag);

                // Update in allTags list if present
                if (allTags != null) {
                    for (int i = 0; i < allTags.size(); i++) {
                        if (allTags.get(i).id == id) {
                            allTags.get(i).name = newName;
                            break;
                        }
                    }
                }
            }
        }
    }

    public void delete(long id) {
        if (tagStore != null) {
            tagStore.delete(id);

            // Remove from allTags list if present
            if (allTags != null) {
                allTags.removeIf(tag -> tag.id == id);
            }
        }
    }
}