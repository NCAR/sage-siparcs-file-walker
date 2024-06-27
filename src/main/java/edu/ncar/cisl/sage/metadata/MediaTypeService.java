package edu.ncar.cisl.sage.metadata;

import edu.ncar.cisl.sage.model.EsFileMissing;
import edu.ncar.cisl.sage.model.EsFileTaskIdentifier;
import edu.ncar.cisl.sage.model.MediaType;
import edu.ncar.cisl.sage.repository.EsFileRepository;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MediaTypeService {

    EsFileRepository repository;
    MetadataStrategy metadataStrategy;

    public MediaTypeService(EsFileRepository repository, MetadataStrategy metadataStrategy) {

        this.repository = repository;
        this.metadataStrategy = metadataStrategy;
    }

    public void updateMediaType(EsFileTaskIdentifier esFileTaskIdentifier) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ");

        try {

            MediaType mediaType = new MediaType();
            String value = this.metadataStrategy.calculateMetadata(esFileTaskIdentifier.getPath());
            mediaType.setMediaType(value);
            mediaType.setDateMediaTypeUpdated(ZonedDateTime.now().format((formatter)));

            this.repository.updateMediaType(esFileTaskIdentifier.getId(), mediaType);

        } catch (Exception e) {

            EsFileMissing esFileMissing = new EsFileMissing();
            esFileMissing.setMissing(true);
            esFileMissing.setDateMissing(ZonedDateTime.now().format((formatter)));

            this.repository.setFileMissing(esFileTaskIdentifier.getId(), esFileMissing);
        }
    }
}
