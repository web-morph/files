package com.github.webmorph.files.image.controller;

import com.github.webmorph.files.file.FileService;
import com.github.webmorph.files.image.model.LocalImage;
import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.Size;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Controller for handling image blur requests via messaging.
 */
@Controller
@RequiredArgsConstructor
public class BlurImageController {
    /**
     * Service for file operations.
     */
    private final FileService fileService;

    /**
     * Handles image blur requests and returns the blurred image's content name.
     *
     * @param request the blur image request containing file name and dimensions
     * @return Mono emitting a key-value pair with the content name
     */
    @MessageMapping("image.resize")
    public Mono<Map.Entry<String, String>> resizeImage(@Payload BlurImageRequest request) {
        return Mono.fromCallable(() -> new LocalImage(this.fileService.pathOf(request.name)).blur(new Size(request.width, request.height)))
                .map(localImage -> new AbstractMap.SimpleEntry<>("content-name", localImage.getInput().toFile().getName()));
    }

    /**
     * Request object for blurring an image.
     *
     * @param name   the name of the image file
     * @param width  the width for the blur
     * @param height the height for the blur
     */
    public record BlurImageRequest(
            String name,
            int width,
            int height
    ) {
    }
}
