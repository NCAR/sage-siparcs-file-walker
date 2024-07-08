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
import edu.ncar.cisl.sage.model.EsFileMissing;
import edu.ncar.cisl.sage.model.EsFileTaskIdentifier;
import edu.ncar.cisl.sage.model.MediaType;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import edu.ncar.cisl.sage.repository.RepositoryException;

import java.io.IOException;
import java.util.List;


public class EsFileRepositoryImpl implements EsFileRepository {

    private final ElasticsearchClient esClient;

    private final BulkIngester<Void> bulkIngester;

    private static final String INDEX = "file-walker-files";

    private final int esQuerySize;

    public EsFileRepositoryImpl(ElasticsearchClient esClient, BulkIngester<Void> bulkIngester, int esQuerySize) {

        this.esClient = esClient;
        this.bulkIngester = bulkIngester;
        this.esQuerySize = esQuerySize;
    }

    @Override
    public List<Hit<EsFileTaskIdentifier>> getFilesWithoutMediaType() {

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
                .field("mediaType"))._toQuery();

        SearchResponse<EsFileTaskIdentifier> response;

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
                            )
                            .from(0)
                            .size(esQuerySize)
                            .sort(so -> so
                                    .field(FieldSort.of(f -> f
                                            .field("dateLastIndexed")
                                            .order(SortOrder.Asc)))),
                    EsFileTaskIdentifier.class
            );

        } catch (IOException e) {

            throw new RepositoryException(e);
        }

        return response.hits().hits();
    }

    public void addFile(String id, EsFile esFile) {

        bulkIngester.add(op -> op
                .update(idx -> idx
                        .index(INDEX)
                        .id(id)
                        .action(a -> a.doc(esFile).upsert(esFile))
                ));
    }

    public void updateMediaType(String id, MediaType mediaType) {

        bulkIngester.add(op -> op
                .update(idx -> idx
                        .index(INDEX)
                        .id(id)
                        .action(a -> a.doc(mediaType))));
    }

    public void setFileMissing(String id, EsFileMissing esFileMissing) {

        bulkIngester.add(op -> op
                .update(idx -> idx
                        .index(INDEX)
                        .id(id)
                        .action(a -> a.doc(esFileMissing))));
    }
}

