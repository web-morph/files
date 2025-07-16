package com.github.webmorph.files.image.event;

import com.github.webmorph.eventbus.event.Event;
import com.github.webmorph.files.image.model.LocalImage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Event representing the upload of an image file.
 */
@Getter
@RequiredArgsConstructor
public class ImageUploadEvent extends Event {
    /**
     * The uploaded image file.
     */
    private final LocalImage image;
}
