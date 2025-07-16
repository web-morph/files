package com.github.webmorph.files.configuration;

import lombok.Getter;
import lombok.Setter;
import org.apache.tika.Tika;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

@Getter
@Setter
@AutoConfiguration
@ConfigurationProperties("webmorph.files")
public class FilesAutoConfiguration {
    private Path folder = Path.of("files");

    @Bean
    @ConditionalOnMissingBean
    public Tika tika() {
        return new Tika();
    }
}
