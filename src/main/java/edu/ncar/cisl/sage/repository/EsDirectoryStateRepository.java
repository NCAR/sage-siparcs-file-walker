package edu.ncar.cisl.sage.repository;

import java.nio.file.Path;

public interface EsDirectoryStateRepository {

    void removeDirectoryState(String id);

    boolean isDirectoryCompleted(String id, Path directory);

    void directoryCompleted(String id, Path directory);
}
