package edu.ncar.cisl.sage.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.model.EsDirectoryState;
import edu.ncar.cisl.sage.repository.EsDirectoryStateRepository;
import edu.ncar.cisl.sage.repository.RepositoryException;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class EsDirectoryStateRepositoryImpl implements EsDirectoryStateRepository {

    private final Map<String, Set<Path>> directoryStateMap;
    private final ElasticsearchClient esClient;

    private static final String INDEX = "file-walker-dir-state";

    public EsDirectoryStateRepositoryImpl(ElasticsearchClient esClient) {

        this.directoryStateMap = new HashMap<>();
        this.esClient = esClient;
    }

    @Override
    public synchronized Map<String, Set<Path>> getAllAsClone() {

        return this.directoryStateMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Set.copyOf(e.getValue())));
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
            }
        } catch( Exception e ) {
            throw new RepositoryException(e);
        }

        return completed;
    }

    @Override
    public synchronized void removeDirectoryState(String id) {

        directoryStateMap.get(id).clear();
    }

    @Override
    public synchronized boolean isDirectoryCompleted(String id, Path dir) {

        this.directoryStateMap.computeIfAbsent(id, k -> new HashSet<>());
        Set<Path> completed = this.directoryStateMap.get(id);

        // query Elasticsearch if completed set is not in memory
        if (completed.isEmpty()) {
            completed = esGetDirectoryState(id);
        }

        return completed.contains(dir);
    }

    @Override
    public synchronized void directoryCompleted(String id, Path dir) {

        Set<Path> directoryState = this.directoryStateMap.get(id);
        directoryState.add(dir);
        directoryState.removeIf(path -> path.startsWith(dir) && !dir.equals(path));
    }
}