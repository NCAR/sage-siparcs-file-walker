package edu.ncar.cisl.sage.main.config;

import edu.ncar.cisl.sage.metadata.mediaType.MediaTypeService;
import edu.ncar.cisl.sage.model.EsTaskIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MediaTypeIntegrationConfig {

    private final QueueChannel mediaTypeChannel;
    private final MediaTypeService mediaTypeService;
    private final int threadCount;

    private static final Logger SI_LOG = LoggerFactory.getLogger("spring-integration");

    public MediaTypeIntegrationConfig(QueueChannel mediaTypeChannel, MediaTypeService mediaTypeService, @Value("${mediaTypeWorkflow.threadCount}") int threadCount) {

        this.mediaTypeChannel = mediaTypeChannel;
        this.mediaTypeService = mediaTypeService;
        this.threadCount = threadCount;
    }

    @Bean
    public IntegrationFlow mediaTypeFlow() {

        SI_LOG.debug("Integration config: {}", this.mediaTypeChannel.getClass().getName()+"@"+Integer.toHexString(this.mediaTypeChannel.hashCode()));

        ExecutorService exec = Executors.newFixedThreadPool(threadCount);
        return IntegrationFlows.fromSupplier(() -> mediaTypeChannel.receive(), c -> c.poller(Pollers
                        .fixedRate(0)
                        .maxMessagesPerPoll(1)))
                .channel(MessageChannels.executor(exec))
                .handle(this::updateMediaType)
                .get();
    }

    private void updateMediaType(org.springframework.messaging.Message<?> message) {

        EsTaskIdentifier esTaskIdentifier = (EsTaskIdentifier) message.getPayload();
        mediaTypeService.updateMediaType(esTaskIdentifier);
    }
}
