package edu.ncar.cisl.sage.config;

import edu.ncar.cisl.sage.filewalker.*;
import edu.ncar.cisl.sage.repository.EsDirectoryStateRepository;
import edu.ncar.cisl.sage.repository.FileWalkerRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.time.Clock;
import java.util.*;

@ConfigurationProperties(prefix = "file-walker-list")
@Configuration
public class FileWalkerRepositoryConfig implements ApplicationEventPublisherAware {

    private List<FileWalkerDto> fileWalkerDtos;
    private EsDirectoryStateRepository esDirectoryStateRepository;

    private ApplicationEventPublisher applicationEventPublisher;

    public FileWalkerRepositoryConfig(List<FileWalkerDto> fileWalkerDtos, EsDirectoryStateRepository esDirectoryStateRepository) {

        this.fileWalkerDtos = fileWalkerDtos;
        this.esDirectoryStateRepository = esDirectoryStateRepository;
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

        FileEventsFileVisitor fileEventsFileVisitor = new FileEventsFileVisitor();
        CompletedFileVisitor completedFileVisitor = new CompletedFileVisitor(fileEventsFileVisitor, esDirectoryStateRepository, dto.getId());
        MetricsFileVisitor metricsFileVisitor = new MetricsFileVisitor(completedFileVisitor);
        IgnorableFileVisitor ignorableFileVisitor = new IgnorableFileVisitor(metricsFileVisitor, dto.getIgnoredPaths());
        FileWalker fileWalker = new FileWalker(Paths.get(dto.getStartPath()), ignorableFileVisitor, metricsFileVisitor, Clock.systemDefaultZone(), dto.getId());

        fileEventsFileVisitor.setApplicationEventPublisher(this.applicationEventPublisher);
        completedFileVisitor.setApplicationEventPublisher(this.applicationEventPublisher);
        fileWalker.setApplicationEventPublisher(this.applicationEventPublisher);

        return fileWalker;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
