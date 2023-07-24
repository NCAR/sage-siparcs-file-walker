package edu.ncar.cisl.sage.config;

import edu.ncar.cisl.sage.filewalker.FileWalker;
import edu.ncar.cisl.sage.filewalker.LoggingFileVisitor;
import edu.ncar.cisl.sage.repository.FileWalkerRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "file-walker-list")
@Configuration
public class FileWalkerRepositoryConfig implements ApplicationEventPublisherAware {

    private List<FileWalkerDto> fileWalkerDtos;

    private ApplicationEventPublisher applicationEventPublisher;

    public FileWalkerRepositoryConfig(List<FileWalkerDto> fileWalkerDtos) {
        this.fileWalkerDtos = fileWalkerDtos;
    }

    public void setFileWalkerDtos(List<FileWalkerDto> fileWalkerDtos) {
        this.fileWalkerDtos = fileWalkerDtos;
    }

    @Bean
    public FileWalkerRepository createRepository() {

        Map<String, FileWalker> fileWalkerMap = new HashMap<>();

        this.fileWalkerDtos.stream()
                .forEach(dto -> fileWalkerMap.put(dto.getId(), createFileWalker(dto)));

        return new FileWalkerRepository(fileWalkerMap);
    }

    private FileWalker createFileWalker(FileWalkerDto dto) {

        LoggingFileVisitor lfv = new LoggingFileVisitor(dto.getIgnoredPaths());
        lfv.setApplicationEventPublisher(this.applicationEventPublisher);

        return new FileWalker(Paths.get(dto.getStartPath()), lfv, Clock.systemDefaultZone());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
