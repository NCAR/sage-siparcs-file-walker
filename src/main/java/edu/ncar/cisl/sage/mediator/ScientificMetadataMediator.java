package edu.ncar.cisl.sage.mediator;

import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.metadata.MediaTypeQueueEmptyEvent;
import edu.ncar.cisl.sage.metadata.ScientificMetadataGateway;
import edu.ncar.cisl.sage.model.EsScientificMetadataTaskIdentifier;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.integration.channel.NullChannel;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScientificMetadataMediator {

    private final EsFileRepository repository;

    private final NullChannel myNullChannel;

    @Autowired
    public ScientificMetadataMediator(EsFileRepository repository, NullChannel myNullChannel) {

        this.repository = repository;
        this.myNullChannel = myNullChannel;
    }

    @Autowired
    private ScientificMetadataGateway scientificMetadataGateway;

    @EventListener
    public void handleQueueRefillNeededEvent(MediaTypeQueueEmptyEvent event) {

        List<Hit<EsScientificMetadataTaskIdentifier>> hitList = this.repository.getFilesWithoutScientificMetadata();

        if(hitList.isEmpty()) {

            threadSleep();

        } else {

            hitList.stream()
                    .forEach(hit -> {

                        EsScientificMetadataTaskIdentifier esScientificMetadataTaskIdentifier = hit.source();
                        esScientificMetadataTaskIdentifier.setId(hit.id());

                        myNullChannel.send((Message<?>) esScientificMetadataTaskIdentifier);
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
