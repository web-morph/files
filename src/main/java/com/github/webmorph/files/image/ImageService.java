package com.github.webmorph.files.image;

import com.github.webmorph.eventbus.EventBus;
import com.github.webmorph.eventbus.annotation.EventHandler;
import com.github.webmorph.eventbus.listener.Listener;
import com.github.webmorph.files.file.event.FileUploadProcessEvent;
import com.github.webmorph.files.file.repository.FileRepository;
import com.github.webmorph.files.image.event.ImageUploadEvent;
import com.github.webmorph.files.image.model.LocalImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Service for handling image file operations such as conversion and upload event processing.
 */
@Service
@RequiredArgsConstructor
public class ImageService implements Listener {
    /**
     * Repository for storing files.
     */
    private final FileRepository repository;
    /**
     * Event bus for dispatching and handling events.
     */
    private final EventBus eventBus;

    /**
     * Handles file upload events for image files. Cancels the default upload process and starts image conversion.
     *
     * @param event the file upload process event
     */
    @EventHandler
    public void onFileUpload(FileUploadProcessEvent event) {
        if (!event.getMimeType().contains("image")) return;
        event.setCanceled(true);
        Path input = event.getInput();
        LocalImage image = new LocalImage(input).convert();
        this.repository.save(image)
                .doOnNext(path -> {
                    event.next(Map.of("content-name", image.getInput().toFile().getName()));
                    event.complete();
                })
                .subscribe(path -> this.eventBus.dispatchEvent(new ImageUploadEvent(new LocalImage(path))));
    }
}
