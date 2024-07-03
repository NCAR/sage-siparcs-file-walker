package edu.ncar.cisl.sage.mediator;

import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.metadata.MediaTypeGateway;
import edu.ncar.cisl.sage.metadata.QueueRefillNeededEvent;
import edu.ncar.cisl.sage.model.EsFile;
import edu.ncar.cisl.sage.model.EsFileTaskIdentifier;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MediaTypeMediator {

    private final EsFileRepository repository;

    @Autowired
    public MediaTypeMediator(EsFileRepository repository) {

        this.repository = repository;
    }

    @Autowired
    private MediaTypeGateway mediaTypeGateway;

    @EventListener
    public void handleQueueRefillNeededEvent(QueueRefillNeededEvent event) {

        List<Hit<EsFile>> hitList = this.repository.getFilesWithoutMediaType();

        if(hitList.isEmpty()) {

            threadSleep();

        } else {

            hitList.stream()
                    .forEach(hit -> {
                        EsFile esFile = hit.source();

                        EsFileTaskIdentifier esFileTaskIdentifier = new EsFileTaskIdentifier();
                        esFileTaskIdentifier.setId(hit.id());
                        esFileTaskIdentifier.setPath(esFile.getPath());

                        mediaTypeGateway.sendToIntegration(esFileTaskIdentifier);
                    });
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