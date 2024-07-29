package edu.ncar.cisl.sage.metadata.mediaType;

import edu.ncar.cisl.sage.model.EsTaskIdentifier;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "mediaTypeChannel")
public interface MediaTypeGateway {

    void sendToIntegration(EsTaskIdentifier data);
}
