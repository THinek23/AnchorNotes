package com.example.anchor;
import java.time.Instant;
public class BaseEntity {
    public long id;

    public Instant createdAt;

    public Instant updatedAt;
    public void touch() {


    }

}
