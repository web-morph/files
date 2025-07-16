package com.github.webmorph.files.image.exception;

/**
 * Exception thrown when an error occurs during image conversion.
 */
public class ImageConvertException extends RuntimeException {
    /**
     * Constructs a new ImageConvertException with the specified detail message.
     *
     * @param message the detail message
     */
    public ImageConvertException(String message) {
        super(message);
    }
}
