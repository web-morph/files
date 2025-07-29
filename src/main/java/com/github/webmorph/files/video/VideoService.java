package com.github.webmorph.files.video;

import com.github.webmorph.eventbus.EventBus;
import com.github.webmorph.eventbus.annotation.EventHandler;
import com.github.webmorph.eventbus.listener.Listener;
import com.github.webmorph.files.ffmpeg.FFmpegService;
import com.github.webmorph.files.file.event.FileUploadProcessEvent;
import com.github.webmorph.files.file.repository.FileRepository;
import com.github.webmorph.files.image.model.LocalImage;
import com.github.webmorph.files.video.event.VideoUploadEvent;
import com.github.webmorph.files.video.model.LocalVideo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Service for handling video file operations such as conversion, preview generation, and upload event processing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService implements Listener {
    /**
     * FFmpeg service for video processing.
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
     * Handles file upload events for video files. Cancels the default upload process, generates a preview, and starts video conversion.
     *
     * @param event the file upload process event
     */
    @SneakyThrows
    @EventHandler
    public void onFileUpload(FileUploadProcessEvent event) {
        if (!event.getMimeType().contains("video")) return;
        event.setCanceled(true);
        Path input = event.getInput();
        this.preview(input)
                .flatMap(this.repository::save)
                .doOnNext(previewPath -> event.next(Map.of("preview-name", previewPath.toFile().getName())))
                .zipWhen(previewPath -> this.convert(input, percent -> event.next(Map.of("progress", percent.toString())))
                        .flatMap(this.repository::save))
                .doOnNext(tuple -> event.next(Map.of("content-name", tuple.getT2().toFile().getName())))
                .subscribe(tuple -> {
                    event.complete();
                    this.eventBus.dispatchEvent(new VideoUploadEvent(new LocalImage(tuple.getT1()), new LocalVideo(tuple.getT2())));
                });
    }

    /**
     * Converts the input video file to HLS-playlist format and returns a Mono of the resulting LocalVideo.
     *
     * @param input      the input file path
     * @param onProgress consumer for progress updates (percent)
     * @return Mono emitting the converted LocalVideo
     */
    public Mono<LocalVideo> convert(Path input, Consumer<Integer> onProgress) {
        Path playlist = input.resolveSibling(UUID.randomUUID().toString());
        Path segment = this.repository.getFolder().resolve(UUID.randomUUID().toString());
        return this.ffmpegService.ffmpeg(
                        "-y",                                                                                             // overwrite without interactive
                        "-i", input.toString(),                                                                                            // input file
                        "-c:v", "libx264",                                                                                      // Codec H.264
                        "-preset", "medium",                                                                                    // speed/quality
                        "-tune", "fastdecode",                                                                                  // fast decode
                        "-crf", "20",                                                                                           // CRF count 20
                        // full-hd is max available resolution
                        "-vf", "scale='min(1920,iw)':'min(1080,ih)':force_original_aspect_ratio=decrease",
                        "-c:a", "aac",                                                                                          // audio AAC
                        "-b:a", "128k",                                                                                         // 320 kbps
                        "-ac", "2",                                                                                             // stereo
                        "-ar", "48000",                                                                                         // 48 kHz
                        "-movflags", "+faststart",                                                                              // web-optimization
                        "-progress", "-", // чтобы выдавал регулярно key=value
                        "-nostats",
                        "-f", "hls",                                                                                            // container format
                        "-hls_time", "5",                                                                                     // segment duration
                        "-hls_list_size", "0",                                                                                 // all segments in the playlist
                        "-hls_segment_filename", segment.toString() + "-%04d",
                        playlist.toString()                                             // output file
                )
                .doOnNext(onProgress)
                .then(Mono.just(new LocalVideo(playlist)));
    }

    /**
     * Generates a preview image (first frame) from the input video file and returns a Mono of the resulting LocalImage.
     *
     * @param input the input video file path
     * @return Mono emitting the generated LocalImage preview
     */
    public Mono<LocalImage> preview(Path input) {
        Path output = input.resolveSibling(UUID.randomUUID().toString());
        return this.ffmpegService.ffmpeg(
                "-y", // overwrite without interactive
                "-i", input.toString(), // input file
                "-vf", "scale='min(1920,iw)':'min(1080,ih)':force_original_aspect_ratio=decrease", // full-hd is max available resolution
                "-vframes", "1", // only first frame
                "-f", "webp", // container format
                output.toString() // output file
        ).then(Mono.just(new LocalImage(output)));
    }
}
