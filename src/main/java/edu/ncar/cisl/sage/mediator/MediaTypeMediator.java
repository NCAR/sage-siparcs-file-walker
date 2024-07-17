package edu.ncar.cisl.sage.mediator;

import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.metadata.mediaType.MediaTypeGateway;
import edu.ncar.cisl.sage.metadata.mediaType.MediaTypeQueueEmptyEvent;
import edu.ncar.cisl.sage.model.EsMediaTypeTaskIdentifier;
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
    public void handleQueueRefillNeededEvent(MediaTypeQueueEmptyEvent event) {

        List<Hit<EsMediaTypeTaskIdentifier>> hitList = this.repository.getFilesWithoutMediaType();

        if(hitList.isEmpty()) {

            threadSleep();

        } else {

            hitList.stream()
                    .forEach(hit -> {

                        EsMediaTypeTaskIdentifier esMediaTypeTaskIdentifier = hit.source();
                        esMediaTypeTaskIdentifier.setId(hit.id());
                        mediaTypeGateway.sendToIntegration(esMediaTypeTaskIdentifier);
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
