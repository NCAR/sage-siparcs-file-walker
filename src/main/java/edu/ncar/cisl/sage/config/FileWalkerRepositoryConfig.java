package edu.ncar.cisl.sage.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import edu.ncar.cisl.sage.filewalker.*;
import edu.ncar.cisl.sage.repository.EsDirStateRepository;
import edu.ncar.cisl.sage.repository.FileWalkerRepository;
import edu.ncar.cisl.sage.repository.impl.EsDirStateRepositoryImpl;
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
    private EsDirStateRepository esDirStateRepository;

    private ApplicationEventPublisher applicationEventPublisher;

    public FileWalkerRepositoryConfig(List<FileWalkerDto> fileWalkerDtos, EsDirStateRepository esDirStateRepository) {

        this.fileWalkerDtos = fileWalkerDtos;
        this.esDirStateRepository = esDirStateRepository;
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

        CompositeFileVisitor compositeFileVisitor = new CompositeFileVisitor(fileEventsFileVisitor,esDirStateRepository,dto.getId());

        MetricsFileVisitor metricsFileVisitor = new MetricsFileVisitor(compositeFileVisitor,dto.getIgnoredPaths());
        fileEventsFileVisitor.setApplicationEventPublisher(this.applicationEventPublisher);
        compositeFileVisitor.setApplicationEventPublisher(this.applicationEventPublisher);

        return new FileWalker(Paths.get(dto.getStartPath()), metricsFileVisitor, Clock.systemDefaultZone());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
