package edu.ncar.cisl.sage.metadata.mediaType;

import org.springframework.context.ApplicationEvent;

public class MediaTypeQueueEmptyEvent extends ApplicationEvent {

    public MediaTypeQueueEmptyEvent(Object source) {
        super(source);
    }

}
