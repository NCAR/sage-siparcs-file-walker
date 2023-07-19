package edu.ncar.cisl.sage.mediaType;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.apache.tika.metadata.Metadata;
import edu.ncar.cisl.sage.model.EsFile;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.mime.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class CheckMediaType {

    private final ElasticsearchClient esClient;

    @Autowired
    public CheckMediaType(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    //@Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void searchMediaType() {

        System.out.println(System.currentTimeMillis() + " start");
//            SearchResponse<EsFile> response = esClient.search(s -> s
//                            .index("files")
//                            .from("error", JsonData.of("false"))
//                            .from("directory", JsonData.of("false"))
//                            .from("mediaType", JsonData.of(null)),
//                    EsFile.class
//            );

//            SearchResponse<EsFile> response = esClient.search(r -> r
//                            .index("files")
//                            .params("error", JsonData.of("false"))
//                            .params("directory", JsonData.of("false"))
//                            .params("mediaType", JsonData.of(null)),
//                    EsFile.class
//            );

//            SearchResponse response = esClient.prepareSearch("files")
////                    .setTypes("type1", "type2")
////                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                    .setQuery(QueryBuilders.termQuery("error", "false"))
//                    .set// Query
//                    .setFilter(FilterBuilders.rangeFilter("age").from(12).to(18))   // Filter
//                    .setFrom(0).setSize(60).setExplain(true)
//                    .execute()
//                    .actionGet();

//            QueryBuilder query = QueryBuilders.boolQuery()
//                    .filter(QueryBuilders.termsQuery("error", "false"))
//                    .filter(QueryBuilders.termsQuery("directory", "false"))
//                    .filter(QueryBuilders.termsQuery("mediaType", null));
//            SearchResponse resp = esClient.prepareSearch().setQuery(query).get();

//            List<Hit<EsFile>> hits = response.hits().hits();
//            for (Hit<EsFile> hit: hits) {
//                EsFile esFile = hit.source();
//                assert esFile != null;
//                System.out.println(esFile.getDateLastIndexed());
//            }
            //System.out.println(response.fields().get("path"));
        try {
            Query byError = MatchQuery.of(m -> m
                    .field("error")
                    .query(false)
            )._toQuery();

            Query byDirectory = MatchQuery.of(ma -> ma
                    .field("directory")
                    .query(false)
            )._toQuery();

            Query mediaTypeExists = ExistsQuery.of(q -> q
                    .field("mediaType"))._toQuery();

//            Query byMediaType = MatchQuery.of(q -> q
//                    .field("mediaType")
//                    .query(o -> o.nullValue()))._toQuery();

            SearchResponse<EsFile> response;

            try {
                response = esClient.search(s -> s
                                .index("files")
                                .query(q -> q
                                        .bool(b -> b
                                                .must(byError)
                                                .must(byDirectory)
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

                throw new RuntimeException(e);
            }

            List<Hit<EsFile>> esFileHitList = response.hits().hits();

            esFileHitList.stream().forEach(esFileHit -> {
                System.out.println(esFileHit.id());
                updateMediaType(esFileHit.source(), esFileHit.id());
            });

        } catch (Exception e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() + " end");
    }

    public void updateMediaType(EsFile esFile, String id) {

        esFile.setMediaType(calculateMediaType(esFile.getPath()));

        try {

            esClient.update(u -> u
                            .index("files")
                            .id(id)
                            .doc(esFile),
                    EsFile.class
            );

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    public String calculateMediaType(Path path) {

        String value = "undefined";

        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(path))) {

            Detector detector = new DefaultDetector();
            Metadata metadata = new Metadata();

            MediaType mediaType = detector.detect(inputStream, metadata);

            //System.out.println(String.format("%s %s", path, mediaType));

            value = mediaType.toString();

        } catch (IOException e) {

            throw new RuntimeException(e);
        } finally {

            return value;
        }
    }
}
