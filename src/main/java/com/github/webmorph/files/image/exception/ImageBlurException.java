package com.github.webmorph.files.image.exception;

/**
 * Exception thrown when an error occurs during image blurring.
 */
public class ImageBlurException extends RuntimeException {
    /**
     * Constructs a new ImageBlurException with the specified detail message.
     *
     * @param message the detail message
     */
    public ImageBlurException(String message) {
        super(message);
    }
}
