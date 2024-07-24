package edu.ncar.cisl.sage.metadata.scientificMetadata;

import edu.ncar.cisl.sage.model.*;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScientificMetadataService {

    private final EsFileRepository repository;
    private final ScientificMetadataFacade scientificMetadataFacade;

    private static final Logger LOG = LoggerFactory.getLogger(ScientificMetadataService.class);

    public ScientificMetadataService(EsFileRepository repository, ScientificMetadataFacade scientificMetadataFacade) {

        this.repository = repository;
        this.scientificMetadataFacade = scientificMetadataFacade;
    }

    public void updateScientificMetadata(EsScientificMetadataTaskIdentifier esScientificMetadataTaskIdentifier) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ");

        String filePath = esScientificMetadataTaskIdentifier.getPath().toString();
        String id = esScientificMetadataTaskIdentifier.getId();

        LOG.debug("Scientific Metadata calculation id: {}", id);

        try {

            // create and populate ScientificMetadata
            ScientificMetadata scientificMetadata = new ScientificMetadata();

//            List<String> standardNames = scientificMetadataFacade.getStandardNames(filePath);
//            scientificMetadata.setStandard_name(standardNames);

            scientificMetadata.setVariables(scientificMetadataFacade.getVariables(filePath));

            scientificMetadata.setContact(scientificMetadataFacade.getGlobalAttributes(filePath,"contact"));
            scientificMetadata.setAuthor(scientificMetadataFacade.getGlobalAttributes(filePath,"author"));

            // Elasticsearch update
            EsScientificMetadata esScientificMetadata = new EsScientificMetadata();
            esScientificMetadata.setScientificMetadata(scientificMetadata);
            esScientificMetadata.setDateScientificMetadataUpdated(ZonedDateTime.now().format((formatter)));

            this.repository.updateScientificMetadata(id, esScientificMetadata);

        } catch (Exception e) {

            EsFileMissing esFileMissing = new EsFileMissing();
            esFileMissing.setMissing(true);
            esFileMissing.setDateMissing(ZonedDateTime.now().format((formatter)));

            this.repository.setFileMissing(id, esFileMissing);
        }
    }
}
