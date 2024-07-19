package edu.ncar.cisl.sage.metadata.mediaType;

import edu.ncar.cisl.sage.model.EsFileMissing;
import edu.ncar.cisl.sage.model.EsMediaTypeTaskIdentifier;
import edu.ncar.cisl.sage.model.MediaType;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MediaTypeService {

    private final EsFileRepository repository;
    private final MediaTypeStrategy mediaTypeStrategy;

    private static final Logger LOG = LoggerFactory.getLogger(MediaTypeService.class);

    public MediaTypeService(EsFileRepository repository, MediaTypeStrategy mediaTypeStrategy) {

        this.repository = repository;
        this.mediaTypeStrategy = mediaTypeStrategy;
    }

    public void updateMediaType(EsMediaTypeTaskIdentifier esMediaTypeTaskIdentifier) {

        LOG.debug("Media type calculation id: {}", esMediaTypeTaskIdentifier.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ");

        try {

            MediaType mediaType = new MediaType();
            String value = this.mediaTypeStrategy.calculateMetadata(esMediaTypeTaskIdentifier.getPath());
            mediaType.setMediaType(value);
            mediaType.setDateMediaTypeUpdated(ZonedDateTime.now().format((formatter)));

            this.repository.updateMediaType(esMediaTypeTaskIdentifier.getId(), mediaType);

        } catch (Exception e) {

            EsFileMissing esFileMissing = new EsFileMissing();
            esFileMissing.setMissing(true);
            esFileMissing.setDateMissing(ZonedDateTime.now().format((formatter)));

            this.repository.setFileMissing(esMediaTypeTaskIdentifier.getId(), esFileMissing);
        }
    }
}
