package edu.ncar.cisl.sage.metadata.scientificMetadata;

import edu.ncar.cisl.sage.model.EsTaskIdentifier;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "scientificMetadataChannel")
public interface ScientificMetadataGateway {

    void sendToIntegration(EsTaskIdentifier esTaskIdentifier);
}
