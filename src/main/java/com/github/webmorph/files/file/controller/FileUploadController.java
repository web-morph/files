package com.github.webmorph.files.file.controller;

import com.github.webmorph.files.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Controller for handling file upload requests via RSocket messaging.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class FileUploadController {
    /**
     * Service for file operations.
     */
    private final FileService service;

    /**
     * Handles file upload messages and delegates to the FileService.
     *
     * @param chunks Flux of DataBuffer representing file chunks
     * @return Flux emitting progress and metadata as key-value pairs
     */
    @MessageMapping("file.upload")
    public Flux<Map.Entry<String, String>> upload(@Payload Flux<DataBuffer> chunks) {
        return this.service.upload(chunks);
    }
}