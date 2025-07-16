package com.github.webmorph.files.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.Loader;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@Slf4j
@AutoConfiguration
public class FFmpegAutoConfiguration {
    @PostConstruct
    public void mute() {
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
    }

    @Bean("ffmpeg_path")
    public String ffmpegPath() throws IOException {
        try {
            return Loader.load(Class.forName("org.bytedeco.ffmpeg.ffmpeg"));
        } catch (Throwable throwable) {
            throw new IOException("Missing dependency \"org.bytedeco:ffmpeg:7.1.1-1.5.12:" + Loader.getPlatform() +
                    "-gpl\" on the classpath. Please add it to use ffmpeg.");
        }
    }
}
