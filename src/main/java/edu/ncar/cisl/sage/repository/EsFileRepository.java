package edu.ncar.cisl.sage.repository;

import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.model.EsFile;
import edu.ncar.cisl.sage.model.EsFileMissing;
import edu.ncar.cisl.sage.model.EsFileTaskIdentifier;
import edu.ncar.cisl.sage.model.MediaType;

import java.util.List;

public interface EsFileRepository {

    List<Hit<EsFileTaskIdentifier>> getFilesWithoutMediaType();

    void addFile(String id, EsFile esFile);

    void updateMediaType(String id, MediaType mediaType);

    void setFileMissing(String id, EsFileMissing esFileMissing);
}
