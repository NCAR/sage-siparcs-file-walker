package edu.ncar.cisl.sage.repository;

import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.model.EsFile;

import java.util.List;

public interface EsFileRepository {

    //void addEsFile(EsFile esFile);

    List<Hit<EsFile>> getFilesWithoutMediaType();

    void updateFile(String id, EsFile esFile);

    void addFile(String id, EsFile esFile);
}
