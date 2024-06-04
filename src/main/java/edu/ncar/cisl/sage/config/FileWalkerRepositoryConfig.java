package edu.ncar.cisl.sage.config;

import edu.ncar.cisl.sage.filewalker.*;
import edu.ncar.cisl.sage.repository.FileWalkerRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.*;

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

        List<FileVisitor<Path>> visitors = new ArrayList<>();
        Set<Path> completed = new HashSet<>();
        Set<Path> inProgress = new HashSet<>();

        FileEventsFileVisitor fileEventsFileVisitor = new FileEventsFileVisitor();
        DirStateFileVisitor dirStateFileVisitor = new DirStateFileVisitor(completed,inProgress);
        visitors.add(fileEventsFileVisitor);
        visitors.add(dirStateFileVisitor);

        CompositeFileVisitor compositeFileVisitor = new CompositeFileVisitor(visitors);
        MetricsFileVisitor metricsFileVisitor = new MetricsFileVisitor(compositeFileVisitor,dto.getIgnoredPaths());
        fileEventsFileVisitor.setApplicationEventPublisher(this.applicationEventPublisher);

        return new FileWalker(Paths.get(dto.getStartPath()), metricsFileVisitor, Clock.systemDefaultZone());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
