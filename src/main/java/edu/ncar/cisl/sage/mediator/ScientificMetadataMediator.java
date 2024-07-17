package edu.ncar.cisl.sage.mediator;

import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.metadata.scientificMetadata.ScientificMetadataGateway;
import edu.ncar.cisl.sage.metadata.scientificMetadata.ScientificMetadataQueueEmptyEvent;
import edu.ncar.cisl.sage.model.EsScientificMetadataTaskIdentifier;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScientificMetadataMediator {

    private final EsFileRepository repository;

    @Autowired
    public ScientificMetadataMediator(EsFileRepository repository) {

        this.repository = repository;
    }

    @Autowired
    private ScientificMetadataGateway scientificMetadataGateway;

    @EventListener
    public void handleQueueRefillNeededEvent(ScientificMetadataQueueEmptyEvent event) {

        List<Hit<EsScientificMetadataTaskIdentifier>> hitList = this.repository.getFilesWithoutScientificMetadata();

        if(hitList.isEmpty()) {

            threadSleep();

        } else {

            hitList.stream()
                    .forEach(hit -> {

                        EsScientificMetadataTaskIdentifier esScientificMetadataTaskIdentifier = hit.source();
                        esScientificMetadataTaskIdentifier.setId(hit.id());
                        scientificMetadataGateway.sendToIntegration(esScientificMetadataTaskIdentifier);
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
