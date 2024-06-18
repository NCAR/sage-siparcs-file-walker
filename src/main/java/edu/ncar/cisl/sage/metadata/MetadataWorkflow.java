package edu.ncar.cisl.sage.metadata;

import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.model.EsFile;
import edu.ncar.cisl.sage.model.MediaType;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MetadataWorkflow {

    private final HttpMessageConverters messageConverters;
    EsFileRepository repository;
    MetadataStrategy metadataStrategy;

    public MetadataWorkflow(EsFileRepository repository, MetadataStrategy metadataStrategy, HttpMessageConverters messageConverters) {

        this.repository = repository;
        this.metadataStrategy = metadataStrategy;
        this.messageConverters = messageConverters;
    }

    @Scheduled(fixedDelay = 50, initialDelay = 10000)
    private void execute() {

        List<Hit<EsFile>> hitList = this.repository.getFilesWithoutMediaType();

        if(hitList.isEmpty()) {

            threadSleep();

        } else {

            hitList.stream()
                .forEach(hit -> {
                    EsFile esFile = hit.source();

                    MediaType mediaType = new MediaType();
                    this.getMediaType(mediaType, esFile);

                    this.repository.updateMediaType(hit.id(),mediaType);
                });
        }
    }

    private void getMediaType(MediaType mediaType, EsFile esFile) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ");

        try {

            String value = this.metadataStrategy.calculateMetadata(esFile.getPath());
            mediaType.setMediaType(value);
            mediaType.setDateMediaTypeUpdated(ZonedDateTime.now().format((formatter)));

        } catch (NoSuchFileException e) {

            esFile.setMissing(true);
            esFile.setDateMissing(ZonedDateTime.now().format((formatter)));

        }
    }

    private static void threadSleep() {

        try {

            Thread.sleep(60000);

        } catch (InterruptedException e) {

            // Ignored...
        }
    }
}
