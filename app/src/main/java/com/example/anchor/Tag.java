package com.example.anchor;

public class Tag extends BaseEntity {
    public String name;

    public Integer color;

    public void rename(String newName) {
        this.name = newName;
    }

    public void setColor(Integer argb) {
        this.color = argb;

    }
}
