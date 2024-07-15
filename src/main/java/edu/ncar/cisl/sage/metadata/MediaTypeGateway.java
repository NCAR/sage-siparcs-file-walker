package edu.ncar.cisl.sage.metadata;

import edu.ncar.cisl.sage.model.EsMediaTypeTaskIdentifier;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "mediaTypeChannel")
public interface MediaTypeGateway {

    void sendToIntegration(EsMediaTypeTaskIdentifier data);
}
