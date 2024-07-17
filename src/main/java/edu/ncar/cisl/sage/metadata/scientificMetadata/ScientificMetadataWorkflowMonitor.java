package edu.ncar.cisl.sage.metadata.scientificMetadata;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.scheduling.annotation.Scheduled;

public class ScientificMetadataWorkflowMonitor implements ApplicationEventPublisherAware {

    private final QueueChannel scientificMetadataChannel;
    private final boolean enabled;

    private ApplicationEventPublisher applicationEventPublisher;

    public ScientificMetadataWorkflowMonitor(QueueChannel scientificMetadataChannel, boolean enabled) {

        this.scientificMetadataChannel = scientificMetadataChannel;
        this.enabled = enabled;
    }

    @Scheduled(initialDelay = 3000, fixedRateString = "${scientificMetadataWorkflow.scheduledTaskFixedRate}")
    public void checkQueueChannel() {

        if (enabled && scientificMetadataChannel.getQueueSize() == 0) {

            ScientificMetadataQueueEmptyEvent event = new ScientificMetadataQueueEmptyEvent(this);
            applicationEventPublisher.publishEvent(event);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
