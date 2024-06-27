package edu.ncar.cisl.sage.metadata;

import org.springframework.context.ApplicationEvent;

public class QueueRefillNeededEvent extends ApplicationEvent {

    public QueueRefillNeededEvent(Object source) {
        super(source);
    }
}
