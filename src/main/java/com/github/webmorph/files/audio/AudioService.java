package com.github.webmorph.files.audio;

import com.github.webmorph.eventbus.EventBus;
import com.github.webmorph.eventbus.annotation.EventHandler;
import com.github.webmorph.eventbus.listener.Listener;
import com.github.webmorph.files.audio.event.AudioUploadEvent;
import com.github.webmorph.files.audio.model.LocalAudio;
import com.github.webmorph.files.ffmpeg.FFmpegService;
import com.github.webmorph.files.file.event.FileUploadProcessEvent;
import com.github.webmorph.files.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Service for handling audio file operations such as conversion and upload event processing.
 */
@Service
@RequiredArgsConstructor
public class AudioService implements Listener {
    /**
     * FFmpeg service for audio processing.
     */
    private final FFmpegService ffmpegService;
    /**
     * Repository for storing files.
     */
    private final FileRepository repository;
    /**
     * Event bus for dispatching and handling events.
     */
    private final EventBus eventBus;

    /**
     * Handles file upload events for audio files. Cancels the default upload process and starts audio conversion.
     *
     * @param event the file upload process event
     */
    @SneakyThrows
    @EventHandler
    public void onFileUpload(FileUploadProcessEvent event) {
        if (!event.getMimeType().contains("audio")) return;
        event.setCanceled(true);
        Path input = event.getInput();
        this.convert(input, percent -> event.next(new AbstractMap.SimpleEntry<>("progress", percent.toString())))
                .flatMap(this.repository::save)
                .doOnNext(path -> event.next(new AbstractMap.SimpleEntry<>("content-name", path.toFile().getName())))
                .subscribe(path -> {
                    event.complete();
                    this.eventBus.dispatchEvent(new AudioUploadEvent(new LocalAudio(path)));
                });
    }

    /**
     * Converts the input audio file to AAC format and returns a Mono of the resulting LocalAudio.
     *
     * @param input      the input file path
     * @param onProgress consumer for progress updates (percent)
     * @return Mono emitting the converted LocalAudio
     */
    public Mono<LocalAudio> convert(Path input, Consumer<Integer> onProgress) {
        Path output = input.resolveSibling(UUID.randomUUID().toString());
        return this.ffmpegService.ffmpeg(
                        "-y",                                                                                             // overwrite without interactive
                        "-i", input.toString(),                                                                                            // input file
                        "-vn",
                        "-c:a", "aac",                                                                                          // audio AAC
                        "-b:a", "128k",                                                                                         // 320 kbps
                        "-ac", "2",                                                                                             // stereo
                        "-ar", "48000",                                                                                         // 48 kHz
                        "-movflags", "+faststart",                                                                              // web-optimization
                        "-progress", "-",
                        "-nostats",
                        "-f", "mp4",                                                                                            // container format
                        output.toString()                                                                                                  // output file
                )
                .doOnNext(onProgress)
                .then(Mono.just(new LocalAudio(output)));
    }
}
