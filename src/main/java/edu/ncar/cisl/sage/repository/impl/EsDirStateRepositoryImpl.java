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
import java.util.*;

public class EsDirStateRepositoryImpl implements EsDirStateRepository {

    private final Map<String, DirectoryState> directoryStateMap = new HashMap<>();
    private final ElasticsearchClient esClient;
    private final BulkIngester<Void> bulkIngester;
    private static final String INDEX = "file-walker-dir-state";

    public EsDirStateRepositoryImpl(ElasticsearchClient esClient, BulkIngester<Void> bulkIngester) {

        this.esClient = esClient;
        this.bulkIngester = bulkIngester;

        // Test
        DirectoryState test = new DirectoryState();
        directoryStateMap.put("music", test);
        test.completed.add(Path.of("/Users/phuongan/Music/Music"));
    }

    @Override
    public List<Hit<EsDirState>> getDirState(String id) throws IOException {

        SearchResponse<EsDirState> response = esClient.search(s -> s
                        .index(INDEX)
                        .query(q -> q
                                .match(t -> t
                                        .field(id)
                                        .query(id)
                                )
                        ),
                EsDirState.class
        );
        List<Hit<EsDirState>> hitList = response.hits().hits();
        return hitList;
    }

    @Override
    public void updateDirState(String id) throws RuntimeException, IOException {

        EsDirState esDirState = createEsDirState(id);

        bulkIngester.add(op -> op
                .index(idx -> idx
                        .index(INDEX)
                        .document(esDirState)
                        .id(id)
                )
        );
    }

    public EsDirState createEsDirState(String id){

        EsDirState esDirState = new EsDirState();

        DirectoryState directoryState = this.directoryStateMap.get(id);

        esDirState.setId(id);
        esDirState.setCompleted(directoryState.getCompleted());
        esDirState.setDateStarted(String.valueOf(ZonedDateTime.now(ZoneId.systemDefault())));
        esDirState.setDateUpdated(String.valueOf(ZonedDateTime.now(ZoneId.systemDefault())));

        return esDirState;
    }

    @Override
    public boolean isCompleted(String id, Path dir) {

        boolean completed = false;
        if (directoryStateMap.containsKey(id)) {
            completed = this.directoryStateMap.get(id).getCompleted().contains(dir);
        }
        return completed;
    }

    @Override
    public void directoryCompleted(String id, Path dir) throws IOException {

        this.directoryStateMap.computeIfAbsent(id, k -> new DirectoryState());
        DirectoryState directoryState = this.directoryStateMap.get(id);
        directoryState.completed.add(dir);
        directoryState.completed.removeIf(path -> path.startsWith(dir) && !dir.equals(path));

        System.out.print("Id: " + id + " : ");
        this.directoryStateMap.get(id).getCompleted().stream()
                .forEach(path -> {System.out.print(path + ", ");});
        System.out.println("\n" + "------------");

        updateDirState(id);
    }

    private static class DirectoryState {

        Set<Path> completed;

        private DirectoryState(){

            completed = new HashSet<>();
        }

        private Set<Path> getCompleted() { return completed; }
    }
}
