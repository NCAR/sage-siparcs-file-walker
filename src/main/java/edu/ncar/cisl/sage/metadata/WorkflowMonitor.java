package edu.ncar.cisl.sage.metadata;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkflowMonitor implements ApplicationEventPublisherAware {

    private final QueueChannel mediaTypeChannel;
    private ApplicationEventPublisher applicationEventPublisher;

    public WorkflowMonitor(QueueChannel mediaTypeChannel) {

        this.mediaTypeChannel = mediaTypeChannel;
    }

    @Scheduled(initialDelay = 3000, fixedRate = 4000)
    public void checkQueueChannel() {

        if (mediaTypeChannel.getQueueSize() == 0) {

            QueueRefillNeededEvent event = new QueueRefillNeededEvent(this);
            applicationEventPublisher.publishEvent(event);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
