package com.github.webmorph.files.image.model;

import com.github.webmorph.files.file.model.LocalFile;
import com.github.webmorph.files.image.exception.ImageBlurException;
import com.github.webmorph.files.image.exception.ImageConvertException;
import com.github.webmorph.files.image.exception.ImageResizeException;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Represents a local image file and provides image processing operations such as resize, blur, and convert.
 */
public class LocalImage extends LocalFile {
    /**
     * Constructs a LocalImage instance from the given file path.
     *
     * @param input the path to the image file
     */
    public LocalImage(Path input) {
        super(input);
    }

    /**
     * Resizes the image to the specified height, maintaining aspect ratio.
     *
     * @param height the new height for the image
     * @return a new LocalImage instance with the resized image
     * @throws ImageResizeException if resizing fails
     */
    public LocalImage resize(int height) {
        return this.transform((image, transformed) ->
                opencv_imgproc.resize(image, transformed, new Size((int) (image.size().aspectRatio() * height), height)), ImageResizeException::new);
    }

    /**
     * Applies a Gaussian blur to the image with the specified size.
     *
     * @param size the blur kernel size
     * @return a new LocalImage instance with the blurred image
     * @throws ImageBlurException if blurring fails
     */
    public LocalImage blur(Size size) {
        return this.transform((image, transformed) ->
                opencv_imgproc.GaussianBlur(image, transformed, size, 0), ImageBlurException::new);
    }

    /**
     * Converts the image (e.g., to WebP format).
     *
     * @return a new LocalImage instance with the converted image
     * @throws ImageConvertException if conversion fails
     */
    public LocalImage convert() {
        return this.transform(Mat::copyTo, ImageConvertException::new);
    }

    /**
     * Returns the size of the image.
     *
     * @return the image size
     */
    public Size size() {
        try (Mat image = opencv_imgcodecs.imread(this.input.toString())) {
            return image.size();
        }
    }

    /**
     * Applies a transformation to the image using the provided consumer and error handler.
     *
     * @param consumer    the transformation logic
     * @param createError function to create an exception on error
     * @return a new LocalImage instance with the transformed image
     */
    public LocalImage transform(
            BiConsumer<Mat, Mat> consumer,
            Function<String, RuntimeException> createError
    ) {
        Mat image = opencv_imgcodecs.imread(this.input.toString());
        if (image.empty()) {
            throw createError.apply("Unable to read image: " + this.input);
        }

        Mat transformed = new Mat();
        consumer.accept(image, transformed);

        String baseName = UUID.randomUUID().toString();
        Path outPath = this.input.resolveSibling(baseName);

        BytePointer buf = new BytePointer();
        IntPointer params = new IntPointer(
                opencv_imgcodecs.IMWRITE_WEBP_QUALITY, 75
        );
        if (!opencv_imgcodecs.imencode(".webp", transformed, buf, params)) {
            throw createError.apply("Failed to encode image as WebP: " + this.input);
        }

        byte[] bytes = buf.getStringBytes();
        try {
            Files.write(outPath, bytes);
        } catch (IOException e) {
            throw createError.apply("Unable to write encoded image to " + outPath);
        }

        return new LocalImage(outPath);
    }

}
