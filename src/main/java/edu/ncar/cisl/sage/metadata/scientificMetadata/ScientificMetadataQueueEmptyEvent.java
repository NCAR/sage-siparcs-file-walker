package edu.ncar.cisl.sage.metadata.scientificMetadata;

import org.springframework.context.ApplicationEvent;

public class ScientificMetadataQueueEmptyEvent extends ApplicationEvent {

    public ScientificMetadataQueueEmptyEvent(Object source) {
        super(source);
    }

}
