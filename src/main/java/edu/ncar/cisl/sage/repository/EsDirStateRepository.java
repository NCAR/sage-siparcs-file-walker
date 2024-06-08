package edu.ncar.cisl.sage.repository;

import co.elastic.clients.elasticsearch.core.search.Hit;
import edu.ncar.cisl.sage.model.EsDirState;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface EsDirStateRepository {

    Set<Path> getDirectoryState(String id) throws IOException;

    void updateDirState(String id) throws IOException;

    boolean isCompleted(String id, Path directory) throws IOException;

    void directoryCompleted(String id, Path directory) throws IOException;
}
