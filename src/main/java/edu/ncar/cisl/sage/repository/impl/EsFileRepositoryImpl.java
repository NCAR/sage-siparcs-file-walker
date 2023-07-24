package edu.ncar.cisl.sage.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.model.EsFile;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import edu.ncar.cisl.sage.repository.RepositoryException;

import java.io.IOException;
import java.util.List;

import static edu.ncar.cisl.sage.WorkingFileVisitorApplication.esIndex;


public class EsFileRepositoryImpl implements EsFileRepository {

    private final ElasticsearchClient esClient;

    private final BulkIngester<Void> bulkIngester;

    private static final String INDEX = "files";

    public EsFileRepositoryImpl(ElasticsearchClient esClient, BulkIngester<Void> bulkIngester) {

        this.esClient = esClient;
        this.bulkIngester = bulkIngester;
    }

    @Override
    public List<Hit<EsFile>> getFilesWithoutMediaType() {

        Query byError = MatchQuery.of(m -> m
                .field("error")
                .query(false)
        )._toQuery();

        Query byDirectory = MatchQuery.of(ma -> ma
                .field("directory")
                .query(false)
        )._toQuery();

        Query byMissing = MatchQuery.of(mat -> mat
                .field("missing")
                .query(false)
        )._toQuery();

        Query mediaTypeExists = ExistsQuery.of(q -> q
                .field("mediaType.keyword"))._toQuery();

        SearchResponse<EsFile> response;

        try {
            response = esClient.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(byError)
                                            .must(byDirectory)
                                            .must(byMissing)
                                            .mustNot(mediaTypeExists)
                                    )
                            ).from(0)
                            .size(50)
                            .sort(so -> so
                                    .field(FieldSort.of(f -> f
                                            .field("dateLastIndexed")
                                            .order(SortOrder.Asc)))),
                    EsFile.class
            );

        } catch (IOException e) {

            throw new RepositoryException(e);
        }

        List<Hit<EsFile>> esFileHitList = response.hits().hits();

        return esFileHitList;
    }

    @Override
    public void updateFile(String id, EsFile esFile) {

        try {

            //esFile.setMediaType(calculateMediaType(esFile.getPath())); >> DO IN STRATEGY/WORKFLOW????

            esClient.update(u -> u
                            .index(INDEX)
                            .id(id)
                            .doc(esFile),
                    EsFile.class
            );

        } catch (IOException e) {

            throw new RepositoryException(e);
        }
    }

    public void addFile(String id, EsFile esFile) {

        bulkIngester.add(op -> op
                .index(idx -> idx
                        .index(INDEX)
                        .document(esFile)
                        .id(id)
                )
        );

    }

}
    //Do all of the indexing and the updating here
