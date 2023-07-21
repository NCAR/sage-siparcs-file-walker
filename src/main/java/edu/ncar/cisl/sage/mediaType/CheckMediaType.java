package edu.ncar.cisl.sage.mediaType;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.apache.tika.Tika;
import edu.ncar.cisl.sage.model.EsFile;
import org.apache.tika.mime.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static edu.ncar.cisl.sage.WorkingFileVisitorApplication.esIndex;

@Component
public class CheckMediaType {

    private final ElasticsearchClient esClient;

    private final Tika tika = new Tika();

    @Autowired
    public CheckMediaType(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    //@Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    public void searchMediaType() {

        //System.out.println(System.currentTimeMillis() + " start");

        try {
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
                                .index(esIndex)
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

                throw new RuntimeException(e);
            }

            List<Hit<EsFile>> esFileHitList = response.hits().hits();

            esFileHitList.stream().forEach(esFileHit -> {
                //System.out.println(esFileHit.id());
                updateMediaType(esFileHit.source(), esFileHit.id());
            });

        } catch (Exception e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
        //System.out.println(System.currentTimeMillis() + " end");
    }

    public void updateMediaType(EsFile esFile, String id) {

        try {

            esFile.setMediaType(calculateMediaType(esFile.getPath()));

            esClient.update(u -> u
                            .index(esIndex)
                            .id(id)
                            .doc(esFile),
                    EsFile.class
            );

        } catch (NoSuchFileException e) {

            this.updateMissingFile(esFile, id);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    public String calculateMediaType(Path path) throws NoSuchFileException {

        String value = MediaType.OCTET_STREAM.toString();

        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(path))) {

            if (Files.notExists(path)) {

                System.out.println("Does not exist: " + path);
            }
            value = this.tika.detect(inputStream, path.getFileName().toString());

        } catch (NoSuchFileException e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();

            throw e;

        } catch (IOException e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

        return value;
    }

    public void updateMissingFile(EsFile esFile, String id) {

        esFile.setMissing(true);

        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ");

        esFile.setDateMissing(zonedDateTime.format((formatter)));

        try {

            esClient.update(u -> u
                            .index(esIndex)
                            .id(id)
                            .doc(esFile),
                    EsFile.class
            );

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}
