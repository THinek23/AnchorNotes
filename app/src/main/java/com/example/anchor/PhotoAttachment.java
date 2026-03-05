package com.example.anchor;

import android.net.Uri;

public class PhotoAttachment extends Attachment {
    public Integer width;
    public Integer height;

    public PhotoAttachment() {
        this.type = AttachmentType.PHOTO;
    }
}
