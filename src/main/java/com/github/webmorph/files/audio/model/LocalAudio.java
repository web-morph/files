package com.github.webmorph.files.audio.model;


import com.github.webmorph.files.file.model.LocalFile;

import java.nio.file.Path;

/**
 * Represents a local audio file, extending LocalFile.
 */
public class LocalAudio extends LocalFile {
    /**
     * Constructs a LocalAudio instance from the given file path.
     *
     * @param input the path to the audio file
     */
    public LocalAudio(Path input) {
        super(input);
    }
}
