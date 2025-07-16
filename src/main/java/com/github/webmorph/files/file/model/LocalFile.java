package com.github.webmorph.files.file.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

/**
 * Represents a local file with its file path.
 */
@Getter
@RequiredArgsConstructor
public class LocalFile {
    /**
     * The path to the local file.
     */
    protected final Path input;
}
