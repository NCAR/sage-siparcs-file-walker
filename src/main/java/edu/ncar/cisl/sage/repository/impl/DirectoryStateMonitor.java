package edu.ncar.cisl.sage.repository.impl;

import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import edu.ncar.cisl.sage.model.EsDirectoryState;
import edu.ncar.cisl.sage.repository.EsDirectoryStateRepository;
import org.springframework.scheduling.annotation.Scheduled;

import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class DirectoryStateMonitor {

    private final EsDirectoryStateRepository directoryStateRepository;
    private final BulkIngester<Void> bulkIngester;

    private static final String INDEX = "file-walker-dir-state";

    public DirectoryStateMonitor(EsDirectoryStateRepository directoryStateRepository, BulkIngester<Void> bulkIngester) {
        this.directoryStateRepository = directoryStateRepository;
        this.bulkIngester = bulkIngester;
    }

    // Start 10 seconds after the applications starts and then run every 30 seconds.
    @Scheduled(initialDelay = 10000, fixedDelayString = "${directoryStateMonitor.scheduledTaskFixedDelay}")
    public void scheduledTask() {

        Map<String, Set<Path>> dirs = this.directoryStateRepository.getAllAsClone();

        dirs.forEach((id, value) -> {

            EsDirectoryState esDirectoryState = this.createEsDirState(id, value);

            bulkIngester.add(op -> op
                    .update(idx -> idx
                            .index(INDEX)
                            .id(id)
                            .action(a -> a.doc(esDirectoryState).upsert(esDirectoryState))
                    ));
        });
    }

    private EsDirectoryState createEsDirState(String id, Set<Path> completed) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ");

        EsDirectoryState esDirectoryState = new EsDirectoryState();

        esDirectoryState.setId(id);
        esDirectoryState.setCompleted(completed);
        //esDirectoryState.setDateCreated();
        esDirectoryState.setDateUpdated(ZonedDateTime.now(ZoneId.systemDefault()).format((formatter)));

        return esDirectoryState;
    }
}
