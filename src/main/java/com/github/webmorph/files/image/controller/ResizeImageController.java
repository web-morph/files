package com.github.webmorph.files.image.controller;

import com.github.webmorph.files.file.FileService;
import com.github.webmorph.files.image.model.LocalImage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Controller for handling image resize requests via messaging.
 */
@Controller
@RequiredArgsConstructor
public class ResizeImageController {
    /**
     * Service for file operations.
     */
    private final FileService fileService;

    /**
     * Handles image resize requests and returns the resized image's content name.
     *
     * @param request the resize image request containing file name and new height
     * @return Mono emitting a key-value pair with the content name
     */
    @MessageMapping("image.resize")
    public Mono<Map.Entry<String, String>> resizeImage(@Payload ResizeImageRequest request) {
        return Mono.fromCallable(() -> new LocalImage(this.fileService.pathOf(request.name)).resize(request.height))
                .map(localImage -> new AbstractMap.SimpleEntry<>("content-name", localImage.getInput().toFile().getName()));
    }

    /**
     * Request object for resizing an image.
     *
     * @param name   the name of the image file
     * @param height the new height for the image
     */
    public record ResizeImageRequest(
            String name,
            int height
    ) {
    }
}
