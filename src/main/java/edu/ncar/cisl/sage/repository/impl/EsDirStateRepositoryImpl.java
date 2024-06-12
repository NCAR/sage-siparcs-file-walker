package edu.ncar.cisl.sage.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.model.EsDirState;
import edu.ncar.cisl.sage.repository.EsDirStateRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EsDirStateRepositoryImpl implements EsDirStateRepository {

    private final Map<String, DirectoryState> directoryStateMap;
    private String dateCreated;
    private final ScheduledExecutorService executor;
    private final ElasticsearchClient esClient;
    private final BulkIngester<Void> bulkIngester;
    private static final String INDEX = "file-walker-dir-state";

    public EsDirStateRepositoryImpl(ElasticsearchClient esClient, BulkIngester<Void> bulkIngester) {

        this.directoryStateMap = new HashMap<>();
        this.dateCreated = reformatDate(ZonedDateTime.now(ZoneId.systemDefault()));
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.esClient = esClient;
        this.bulkIngester = bulkIngester;
    }

    @Override
    public Set<Path> getDirectoryState(String id) throws IOException {

        Set<Path> completed = new HashSet<>();
        SearchResponse<EsDirState> response = esClient.search(s -> s
                        .index(INDEX)
                        .query(q -> q
                                .match(t -> t
                                        .field("id")
                                        .query(id)
                                )
                        ),
                EsDirState.class
        );
        List<Hit<EsDirState>> hitList = response.hits().hits();
        if(!hitList.isEmpty()) {
            EsDirState esDirState = hitList.get(0).source();
            completed = esDirState.getCompleted();
            dateCreated = esDirState.getDateCreated();
        }
        return completed;
    }

    @Override
    public void updateDirectoryState(String id) throws RuntimeException, IOException {

        EsDirState esDirState = createEsDirState(id);

        bulkIngester.add(op -> op
                .index(idx -> idx
                        .index(INDEX)
                        .document(esDirState)
                        .id(id)
                )
        );
    }

    @Override
    public void deleteDirectoryState(String id) {

        directoryStateMap.remove(id);
        executor.schedule(() -> esClient.delete(d -> d.index(INDEX).id(id)), 5, TimeUnit.SECONDS);
    }

    public EsDirState createEsDirState(String id) {

        EsDirState esDirState = new EsDirState();

        esDirState.setId(id);
        esDirState.setCompleted(this.directoryStateMap.get(id).completed);
        esDirState.setDateCreated(dateCreated);
        esDirState.setDateUpdated(reformatDate(ZonedDateTime.now(ZoneId.systemDefault())));

        return esDirState;
    }

    @Override
    public boolean isCompleted(String id, Path dir) throws IOException {

        this.directoryStateMap.computeIfAbsent(id, k -> new DirectoryState());
        Set<Path> completed = this.directoryStateMap.get(id).completed;

        // query Elasticsearch if completed set is not in memory
        if(completed.isEmpty()) {
            completed = getDirectoryState(id);
        }

        return completed.contains(dir);
    }

    @Override
    public void directoryCompleted(String id, Path dir, Path startingPath) throws IOException {

        DirectoryState directoryState = this.directoryStateMap.get(id);
        directoryState.completed.add(dir);
        directoryState.completed.removeIf(path -> path.startsWith(dir) && !dir.equals(path));

        updateDirectoryState(id);
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