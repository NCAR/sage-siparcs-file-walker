package edu.ncar.cisl.sage.metadata;

import edu.ncar.cisl.sage.model.EsFileTaskIdentifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@IntegrationComponentScan
public class IntegrationConfig {

    private final QueueChannel mediaTypeChannel;
    private final MediaTypeService mediaTypeService;
    private final int threadCount;

    public IntegrationConfig(QueueChannel mediaTypeChannel, MediaTypeService mediaTypeService, @Value("${mediaTypeWorkflow.threadCount}") int threadCount) {

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

          EsFileTaskIdentifier esFileTaskIdentifier = (EsFileTaskIdentifier) message.getPayload();
          mediaTypeService.updateMediaType(esFileTaskIdentifier);
    }
}
