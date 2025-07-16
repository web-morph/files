package com.github.webmorph.files.audio.event;

import com.github.webmorph.eventbus.event.Event;
import com.github.webmorph.files.audio.model.LocalAudio;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Event representing the upload of an audio file.
 */
@Getter
@RequiredArgsConstructor
public class AudioUploadEvent extends Event {
    /**
     * The uploaded audio file.
     */
    private final LocalAudio audio;
}
