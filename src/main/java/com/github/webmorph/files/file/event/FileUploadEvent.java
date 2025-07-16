package com.github.webmorph.files.file.event;

import com.github.webmorph.eventbus.event.Event;
import com.github.webmorph.files.file.model.LocalFile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Event representing the upload of a file.
 */
@Getter
@RequiredArgsConstructor
public class FileUploadEvent extends Event {
    /**
     * The uploaded file.
     */
    private final LocalFile file;
}
