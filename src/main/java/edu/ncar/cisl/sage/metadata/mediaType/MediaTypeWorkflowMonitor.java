package edu.ncar.cisl.sage.metadata.mediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.scheduling.annotation.Scheduled;

public class MediaTypeWorkflowMonitor implements ApplicationEventPublisherAware {

    private final QueueChannel mediaTypeChannel;
    private final boolean enabled;

    private ApplicationEventPublisher applicationEventPublisher;

    private static final Logger SI_LOG = LoggerFactory.getLogger("spring-integration");

    public MediaTypeWorkflowMonitor(QueueChannel mediaTypeChannel, boolean enabled) {

        this.mediaTypeChannel = mediaTypeChannel;
        this.enabled = enabled;
    }

    @Scheduled(initialDelay = 3000, fixedDelayString = "${mediaTypeWorkflow.scheduledTaskDelayRate}")
    public void checkQueueChannel() {

        SI_LOG.debug("Monitor: {}", this.mediaTypeChannel.getClass().getName()+"@"+Integer.toHexString(this.mediaTypeChannel.hashCode()));

        if (enabled && this.mediaTypeChannel.getQueueSize() == 0) {

            MediaTypeQueueEmptyEvent event = new MediaTypeQueueEmptyEvent(this);
            this.applicationEventPublisher.publishEvent(event);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
