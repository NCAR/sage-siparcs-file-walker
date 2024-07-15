package edu.ncar.cisl.sage.metadata;

import edu.ncar.cisl.sage.model.EsScientificMetadataTaskIdentifier;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "scientificMetadataChannel")
public interface ScientificMetadataGateway {

    void sendToIntegration(EsScientificMetadataTaskIdentifier esScientificMetadataTaskIdentifier);
}
