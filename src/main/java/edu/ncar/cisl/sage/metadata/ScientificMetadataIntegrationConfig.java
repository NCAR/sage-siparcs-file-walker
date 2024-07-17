package edu.ncar.cisl.sage.metadata;

import edu.ncar.cisl.sage.model.EsScientificMetadataTaskIdentifier;
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
public class ScientificMetadataIntegrationConfig {

    private final QueueChannel scientificMetadataChannel;
    private final ScientificMetadataService scientificMetadataService;
    private final int threadCount;


    public ScientificMetadataIntegrationConfig(QueueChannel scientificMetadataChannel, ScientificMetadataService scientificMetadataService, @Value("${scientificMetadataWorkflow.threadCount}") int threadCount) {

        this.scientificMetadataChannel = scientificMetadataChannel;
        this.scientificMetadataService = scientificMetadataService;
        this.threadCount = threadCount;
    }

    @Bean
    public IntegrationFlow scientificMetadataFlow() {

        ExecutorService exec = Executors.newFixedThreadPool(threadCount);
        return IntegrationFlows.fromSupplier(() -> scientificMetadataChannel.receive(), c -> c.poller(Pollers
                        .fixedRate(0)
                        .maxMessagesPerPoll(1)))
                .channel(MessageChannels.executor(exec))
                .handle(this::updateScientificMetadata)
                .get();
    }

    private void updateScientificMetadata(org.springframework.messaging.Message<?> message) {

        EsScientificMetadataTaskIdentifier esScientificMetadataTaskIdentifier = (EsScientificMetadataTaskIdentifier) message.getPayload();
        scientificMetadataService.updateScientificMetadata(esScientificMetadataTaskIdentifier);
    }
}
