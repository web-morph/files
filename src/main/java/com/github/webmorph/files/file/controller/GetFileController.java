package com.github.webmorph.files.file.controller;

import com.github.webmorph.files.file.FileService;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class GetFileController {
    private final FileService service;
    private final Tika tika;

    @GetMapping("/file/{name}")
    public Mono<ResponseEntity<FileSystemResource>> getFile(@PathVariable(name = "name") String name) {
        return this.service.load(name)
                .zipWhen(resource -> Mono.fromCallable(() -> this.tika.detect(resource.getFile())))
                .map(tuple -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(tuple.getT2()))
                        .body(tuple.getT1()));
    }
}
