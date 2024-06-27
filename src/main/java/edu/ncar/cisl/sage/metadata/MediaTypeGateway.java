package edu.ncar.cisl.sage.metadata;

import edu.ncar.cisl.sage.model.EsFileTaskIdentifier;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "mediaTypeChannel")
public interface MediaTypeGateway {

    void sendToIntegration(EsFileTaskIdentifier data);
}
