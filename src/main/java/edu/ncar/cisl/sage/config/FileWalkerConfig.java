package edu.ncar.cisl.sage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "file-walker-list")
@Configuration
public class FileWalkerConfig {

    private List<FileWalkerDto> fileWalkerDtos;

    public FileWalkerConfig(List<FileWalkerDto> fileWalkerDtos) {
        this.fileWalkerDtos = fileWalkerDtos;
    }

    public void setFileWalkerDtos(List<FileWalkerDto> fileWalkerDtos) {
        this.fileWalkerDtos = fileWalkerDtos;
    }

    @Bean
    public List<FileWalkerDto> getFileWalkerDTOList() {

        return this.fileWalkerDtos;
    }
}
