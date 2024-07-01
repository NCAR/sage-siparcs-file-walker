package edu.ncar.cisl.sage.metadata;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.scheduling.annotation.Scheduled;

public class WorkflowMonitor implements ApplicationEventPublisherAware {

    private final QueueChannel mediaTypeChannel;
    private final boolean enabled;

    private ApplicationEventPublisher applicationEventPublisher;

    public WorkflowMonitor(QueueChannel mediaTypeChannel, boolean enabled) {

        this.mediaTypeChannel = mediaTypeChannel;
        this.enabled = enabled;
    }

    @Scheduled(initialDelay = 3000, fixedRate = 4000)
    public void checkQueueChannel() {

        if (enabled && mediaTypeChannel.getQueueSize() == 0) {

            QueueRefillNeededEvent event = new QueueRefillNeededEvent(this);
            applicationEventPublisher.publishEvent(event);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
