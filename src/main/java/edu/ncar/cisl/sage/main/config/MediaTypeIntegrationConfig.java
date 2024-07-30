package edu.ncar.cisl.sage.main.config;

import edu.ncar.cisl.sage.metadata.mediaType.MediaTypeService;
import edu.ncar.cisl.sage.model.EsTaskIdentifier;
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

    public MediaTypeIntegrationConfig(QueueChannel mediaTypeChannel, MediaTypeService mediaTypeService, @Value("${mediaTypeWorkflow.threadCount}") int threadCount) {

        this.mediaTypeChannel = mediaTypeChannel;
        this.mediaTypeService = mediaTypeService;
        this.threadCount = threadCount;
    }

    @Bean
    public IntegrationFlow mediaTypeFlow() {

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
