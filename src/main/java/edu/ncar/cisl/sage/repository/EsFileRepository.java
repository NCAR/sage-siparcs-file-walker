package edu.ncar.cisl.sage.repository;

import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.model.*;

import java.util.List;

public interface EsFileRepository {

    List<Hit<EsMediaTypeTaskIdentifier>> getFilesWithoutMediaType();

    List<Hit<EsScientificMetadataTaskIdentifier>> getFilesWithoutScientificMetadata();

    void addFile(String id, EsFile esFile);

    void updateMediaType(String id, MediaType mediaType);

    void setFileMissing(String id, EsFileMissing esFileMissing);
}
