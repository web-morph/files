package com.github.webmorph.files.ffmpeg;

import com.github.webmorph.files.ffmpeg.exception.FFmpegException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for executing FFmpeg commands and tracking their progress.
 */
@Service
@RequiredArgsConstructor
public class FFmpegService {
    /**
     * Path to the FFmpeg executable.
     */
    @Qualifier("ffmpeg_path")
    private final String ffmpeg;

    /**
     * Executes an FFmpeg command with the given arguments and emits progress as a Flux of percent complete.
     *
     * @param args FFmpeg command-line arguments
     * @return Flux emitting progress percentage (0-100)
     */
    @SneakyThrows
    public Flux<Integer> ffmpeg(String... args) {
        String[] command = new String[args.length + 1];
        command[0] = this.ffmpeg;
        System.arraycopy(args, 0, command, 1, args.length);
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        Pattern durationPattern = Pattern.compile("^.+Duration:.+(\\d{2}:\\d{2}:\\d{2}).+$");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        AtomicLong duration = new AtomicLong(0);
        return Flux.create(sink -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                for (; ; ) {
                    String line = reader.readLine();
                    if (line == null) break;
                    Matcher matcher = durationPattern.matcher(line);
                    if (matcher.matches()) {
                        duration.set(LocalTime.parse(matcher.group(1), timeFormatter).toSecondOfDay() * 1_000_000L);
                    }
                    int index = line.indexOf("out_time_ms=");
                    if (index != -1) {
                        String msString = line.substring(12);
                        if (msString.equals("N/A")) continue;
                        sink.next(Math.min(100, (int) ((Long.parseLong(msString) / (double) duration.get()) * 100)));
                    }
                }
            } catch (IOException e) {
                sink.error(new FFmpegException(e.getMessage()));
            }
            sink.complete();
        });
    }
}
