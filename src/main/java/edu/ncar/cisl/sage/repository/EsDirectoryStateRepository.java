package edu.ncar.cisl.sage.repository;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public interface EsDirectoryStateRepository {

    Map<String, Set<Path>> getAllAsClone();
    void removeDirectoryState(String id);

    boolean isDirectoryCompleted(String id, Path directory);

    void directoryCompleted(String id, Path directory);
}
