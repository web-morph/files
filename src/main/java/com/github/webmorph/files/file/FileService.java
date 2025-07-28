package com.github.webmorph.files.file;

import com.github.webmorph.eventbus.EventBus;
import com.github.webmorph.eventbus.annotation.EventHandler;
import com.github.webmorph.eventbus.event.EventPriority;
import com.github.webmorph.eventbus.listener.Listener;
import com.github.webmorph.files.file.event.FileUploadEvent;
import com.github.webmorph.files.file.event.FileUploadProcessEvent;
import com.github.webmorph.files.file.model.LocalFile;
import com.github.webmorph.files.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for handling file operations such as upload and event processing.
 */
@Service
@RequiredArgsConstructor
public class FileService implements Listener {
    /**
     * Temporary directory for storing files during upload.
     */
    private final Path tmpDir = Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath();
    /**
     * Event bus for dispatching and handling events.
     */
    private final EventBus eventBus;
    /**
     * Repository for storing files.
     */
    private final FileRepository repository;
    /**
     * Tika instance for MIME type detection.
     */
    private final Tika tika;

    /**
     * Returns the path of a file by name in the repository folder.
     *
     * @param name the file name
     * @return the resolved file path
     */
    public Path pathOf(String name) {
        return this.repository.getFolder().resolve(name);
    }

    /**
     * Handles the upload of file chunks, writes to a temp file, detects MIME type, and dispatches an event.
     *
     * @param chunks Flux of DataBuffer representing file chunks
     * @return Flux emitting progress and metadata as key-value pairs
     */
    public Flux<Map<String, String>> upload(Flux<DataBuffer> chunks) {
        String name = UUID.randomUUID().toString();
        Path tempFile = this.tmpDir.resolve(name);
        return DataBufferUtils.write(chunks, tempFile, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)
                .then(Mono.fromCallable(() -> this.tika.detect(tempFile)))
                .flatMapMany(mimeType -> {
                    FileUploadProcessEvent event = new FileUploadProcessEvent(tempFile, mimeType);
                    Schedulers.boundedElastic().schedule(() -> this.eventBus.dispatchEvent(event));
                    return event.asFlux();
                });
    }

    /**
     * Handles file upload events, saves the file, and dispatches a FileUploadEvent.
     *
     * @param event the file upload process event
     */
    @SneakyThrows
    @EventHandler(priority = EventPriority.HIGH)
    public void onFileUpload(FileUploadProcessEvent event) {
        event.setCanceled(true);
        Path tempFile = event.getInput();
        this.repository.save(new LocalFile(tempFile))
                .doOnNext(path -> {
                    event.next(Map.of("content-name", event.getInput().toFile().getName()));
                    event.complete();
                })
                .subscribe(path -> this.eventBus.dispatchEvent(new FileUploadEvent(new LocalFile(path))));
    }
}
