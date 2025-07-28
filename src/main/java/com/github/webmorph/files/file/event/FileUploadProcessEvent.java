package com.github.webmorph.files.file.event;

import com.github.webmorph.eventbus.event.CancelableEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.nio.file.Path;
import java.util.Map;

/**
 * Event representing the process of uploading a file, supporting progress updates and completion.
 */
@RequiredArgsConstructor
public class FileUploadProcessEvent extends CancelableEvent {
    /**
     * The input file path being uploaded.
     */
    @Getter
    private final Path input;
    /**
     * The MIME type of the file being uploaded.
     */
    @Getter
    private final String mimeType;
    /**
     * Publisher for emitting progress and metadata updates.
     */
    private final Sinks.Many<Map<String, String>> publisher = Sinks.many().unicast().onBackpressureBuffer();

    /**
     * Emits a progress or metadata update.
     *
     * @param next the key-value pair to emit
     */
    public void next(Map<String, String> next) {
        this.publisher.tryEmitNext(next);
    }

    /**
     * Marks the upload process as complete.
     */
    public void complete() {
        this.publisher.tryEmitComplete();
    }

    /**
     * Returns a Flux for subscribing to progress and metadata updates.
     *
     * @return Flux emitting key-value pairs
     */
    public Flux<Map<String, String>> asFlux() {
        return this.publisher.asFlux();
    }
}
