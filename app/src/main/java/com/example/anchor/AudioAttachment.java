package com.example.anchor;

public class AudioAttachment extends Attachment {

    public long durationMs;

    public AudioAttachment() {
        this.type = AttachmentType.AUDIO;
    }

}
