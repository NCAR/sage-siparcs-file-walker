package edu.ncar.cisl.sage.metadata.mediaType;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.scheduling.annotation.Scheduled;

public class MediaTypeWorkflowMonitor implements ApplicationEventPublisherAware {

    private final QueueChannel mediaTypeChannel;
    private final boolean enabled;

    private ApplicationEventPublisher applicationEventPublisher;

    public MediaTypeWorkflowMonitor(QueueChannel mediaTypeChannel, boolean enabled) {

        this.mediaTypeChannel = mediaTypeChannel;
        this.enabled = enabled;
    }

    @Scheduled(initialDelay = 3000, fixedRateString = "${mediaTypeWorkflow.scheduledTaskFixedRate}")
    public void checkQueueChannel() {

        if (enabled && mediaTypeChannel.getQueueSize() == 0) {

            MediaTypeQueueEmptyEvent event = new MediaTypeQueueEmptyEvent(this);
            applicationEventPublisher.publishEvent(event);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
