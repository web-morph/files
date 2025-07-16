package com.github.webmorph.files.image.exception;

/**
 * Exception thrown when an error occurs during image resizing.
 */
public class ImageResizeException extends RuntimeException {
    /**
     * Constructs a new ImageResizeException with the specified detail message.
     *
     * @param message the detail message
     */
    public ImageResizeException(String message) {
        super(message);
    }
}
