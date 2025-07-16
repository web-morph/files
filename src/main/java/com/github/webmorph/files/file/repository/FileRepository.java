package com.github.webmorph.files.file.repository;

import com.github.webmorph.files.configuration.FilesAutoConfiguration;
import com.github.webmorph.files.file.model.LocalFile;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Repository for managing file storage operations in a specified folder.
 */
@Getter
@Repository
public class FileRepository {
    /**
     * The folder where files are stored.
     */
    private final Path folder;

    /**
     * Initializes the repository and ensures the storage folder exists.
     */
    @SneakyThrows
    @PostConstruct
    public void init() {
        Files.createDirectories(this.folder);
    }

    /**
     * Constructs a FileRepository with the given configuration.
     *
     * @param configuration the files auto-configuration
     */
    public FileRepository(FilesAutoConfiguration configuration) {
        this.folder = configuration.getFolder();
    }

    /**
     * Saves a local file to the repository folder.
     *
     * @param file the local file to save
     * @return Mono emitting the path to the saved file
     */
    public Mono<Path> save(LocalFile file) {
        Path input = file.getInput();
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> Files.move(input.toAbsolutePath(), this.folder.resolve(input.getFileName())));
    }

    /**
     * Loads a file as a FileSystemResource by name.
     *
     * @param name the file name
     * @return Mono emitting the FileSystemResource if it exists
     */
    public Mono<FileSystemResource> load(String name) {
        return Mono.just(new FileSystemResource(this.folder.resolve(name)))
                .filter(FileSystemResource::exists);
    }

    /**
     * Checks if a file exists in the repository folder.
     *
     * @param name the file name
     * @return true if the file exists, false otherwise
     */
    public boolean exists(String name) {
        return this.folder.resolve(name).toFile().exists();
    }

    /**
     * Deletes a file by name from the repository folder.
     *
     * @param name the file name
     * @return Mono emitting true if the file was deleted, false otherwise
     */
    public Mono<Boolean> delete(String name) {
        return Mono.fromCallable(() -> this.folder.resolve(name).toFile().delete())
                .subscribeOn(Schedulers.boundedElastic());
    }
}
