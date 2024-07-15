package edu.ncar.cisl.sage.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.model.EsDirectoryState;
import edu.ncar.cisl.sage.repository.EsDirectoryStateRepository;
import edu.ncar.cisl.sage.repository.RepositoryException;

import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EsDirectoryStateRepositoryImpl implements EsDirectoryStateRepository {

    private final Map<String, DirectoryState> directoryStateMap;
    private String dateCreated;
    private final ElasticsearchClient esClient;
    private final BulkIngester<Void> bulkIngester;
    private static final String INDEX = "file-walker-dir-state";

    public EsDirectoryStateRepositoryImpl(ElasticsearchClient esClient, BulkIngester<Void> bulkIngester) {

        this.directoryStateMap = new HashMap<>();
        this.dateCreated = reformatDate(ZonedDateTime.now(ZoneId.systemDefault()));
        this.esClient = esClient;
        this.bulkIngester = bulkIngester;
    }

    private Set<Path> esGetDirectoryState(String id) {

        Set<Path> completed = new HashSet<>();

        try {
            SearchResponse<EsDirectoryState> response = esClient.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .match(t -> t
                                            .field("id")
                                            .query(id)
                                    )
                            ),
                    EsDirectoryState.class
            );
            List<Hit<EsDirectoryState>> hitList = response.hits().hits();
            if(!hitList.isEmpty()) {
                EsDirectoryState esDirectoryState = hitList.get(0).source();
                completed = esDirectoryState.getCompleted();
                dateCreated = esDirectoryState.getDateCreated();
            }
        } catch( Exception e ) {
            throw new RepositoryException(e);
        }

        return completed;
    }

    private void esUpdateDirectoryState(String id) throws RuntimeException {

        EsDirectoryState esDirectoryState = createEsDirState(id);

        bulkIngester.add(op -> op
                .index(idx -> idx
                        .index(INDEX)
                        .document(esDirectoryState)
                        .id(id)
                )
        );
    }

    @Override
    public void removeDirectoryState(String id) {

        directoryStateMap.remove(id);
        bulkIngester.add(op -> op
                .delete(d -> d
                        .index(INDEX)
                        .id(id)
                )
        );
    }

    public EsDirectoryState createEsDirState(String id) {

        EsDirectoryState esDirectoryState = new EsDirectoryState();

        esDirectoryState.setId(id);
        esDirectoryState.setCompleted(this.directoryStateMap.get(id).completed);
        esDirectoryState.setDateCreated(dateCreated);
        esDirectoryState.setDateUpdated(reformatDate(ZonedDateTime.now(ZoneId.systemDefault())));

        return esDirectoryState;
    }

    @Override
    public boolean isDirectoryCompleted(String id, Path dir) {

        this.directoryStateMap.computeIfAbsent(id, k -> new DirectoryState());
        Set<Path> completed = this.directoryStateMap.get(id).completed;

        // query Elasticsearch if completed set is not in memory
        if(completed.isEmpty()) {
            completed = esGetDirectoryState(id);
        }

        return completed.contains(dir);
    }

    @Override
    public void directoryCompleted(String id, Path dir) {

        DirectoryState directoryState = this.directoryStateMap.get(id);
        directoryState.completed.add(dir);
        directoryState.completed.removeIf(path -> path.startsWith(dir) && !dir.equals(path));

        esUpdateDirectoryState(id);
    }

    private String reformatDate(ZonedDateTime zonedDateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ");

        return (zonedDateTime.format((formatter)));
    }

    private static class DirectoryState {

        Set<Path> completed;

        private DirectoryState(){

            completed = new HashSet<>();
        }
    }
}