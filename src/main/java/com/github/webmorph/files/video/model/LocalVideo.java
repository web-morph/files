package com.github.webmorph.files.video.model;

import com.github.webmorph.files.file.model.LocalFile;

import java.nio.file.Path;

/**
 * Represents a local video file, extending LocalFile.
 */
public class LocalVideo extends LocalFile {
    /**
     * Constructs a LocalVideo instance from the given file path.
     *
     * @param input the path to the video file
     */
    public LocalVideo(Path input) {
        super(input);
    }
}