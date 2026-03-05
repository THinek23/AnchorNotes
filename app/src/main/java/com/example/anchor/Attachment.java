package com.example.anchor;

import java.net.URI;
import java.time.Instant;

public class Attachment extends BaseEntity {

    public AttachmentType type;

    public URI uri;

    public String mimetype;

    public Instant createdAtMedia;

    public long noteId;

    public boolean isPhoto() {
        // TODO
        return type == AttachmentType.PHOTO;
    }

    public boolean isAudio() {
        // TODO (In UML Design has Video; however, documentation says it should be photo & Audio)
        return type == AttachmentType.AUDIO;
    }




}
