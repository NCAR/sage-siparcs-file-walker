package edu.ncar.cisl.sage.repository;

import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.model.EsFile;
import edu.ncar.cisl.sage.model.MediaType;

import java.io.IOException;
import java.util.List;

public interface EsFileRepository {

    List<Hit<EsFile>> getFilesWithoutMediaType();

    void addFile(String id, EsFile esFile);

    void updateMediaType(String id, MediaType mediaType);
}
