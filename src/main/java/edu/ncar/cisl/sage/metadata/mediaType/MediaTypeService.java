package edu.ncar.cisl.sage.metadata.mediaType;

import edu.ncar.cisl.sage.model.EsFileMissing;
import edu.ncar.cisl.sage.model.EsTaskIdentifier;
import edu.ncar.cisl.sage.model.EsMediaType;
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

    public void updateMediaType(EsTaskIdentifier esTaskIdentifier) {

        //LOG.debug("Media type calculation id: {}", esTaskIdentifier.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ");

        try {

            EsMediaType esMediaType = new EsMediaType();
            String value = this.mediaTypeStrategy.calculateMetadata(esTaskIdentifier.getPath());
            esMediaType.setMediaType(value);
            esMediaType.setDateMediaTypeUpdated(ZonedDateTime.now().format((formatter)));

            this.repository.updateMediaType(esTaskIdentifier.getId(), esMediaType);

        } catch (Exception e) {

            EsFileMissing esFileMissing = new EsFileMissing();
            esFileMissing.setMissing(true);
            esFileMissing.setDateMissing(ZonedDateTime.now().format((formatter)));

            this.repository.setFileMissing(esTaskIdentifier.getId(), esFileMissing);
        }
    }
}
