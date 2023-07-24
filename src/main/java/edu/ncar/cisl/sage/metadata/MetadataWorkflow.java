package edu.ncar.cisl.sage.metadata;

import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import edu.ncar.cisl.sage.model.EsFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.NoSuchFileException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MetadataWorkflow {

    EsFileRepository repository;

    MetadataStrategy metadataStrategy;

    public MetadataWorkflow(EsFileRepository repository, MetadataStrategy metadataStrategy) {

        this.repository = repository;
        this.metadataStrategy = metadataStrategy;
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

                    this.updateEsFile(esFile);

                    this.repository.updateFile(hit.id(), esFile);
                });
        }
    }

    private void updateEsFile(EsFile esFile) {

        try {

            this.metadataStrategy.calculateMetadata(esFile);

        } catch (NoSuchFileException e) {

            esFile.setMissing(true);

            ZonedDateTime zonedDateTime = ZonedDateTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ");

            esFile.setDateMissing(zonedDateTime.format((formatter)));
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
