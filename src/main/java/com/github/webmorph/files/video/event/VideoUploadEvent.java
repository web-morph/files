package com.github.webmorph.files.video.event;

import com.github.webmorph.eventbus.event.Event;
import com.github.webmorph.files.image.model.LocalImage;
import com.github.webmorph.files.video.model.LocalVideo;
import lombok.RequiredArgsConstructor;

/**
 * Event representing the upload of a video file, including its preview image and video content.
 */
@RequiredArgsConstructor
public class VideoUploadEvent extends Event {
    /**
     * The generated preview image for the video.
     */
    private final LocalImage preview;
    /**
     * The uploaded video file.
     */
    private final LocalVideo video;
}
