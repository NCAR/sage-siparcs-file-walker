package edu.ncar.cisl.sage.repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface EsDirStateRepository {

    Set<Path> getDirectoryState(String id) throws IOException;

    void updateDirectoryState(String id) throws IOException;

    void deleteDirectoryState(String id);

    boolean isCompleted(String id, Path directory) throws IOException;

    void directoryCompleted(String id, Path directory, Path startingPath) throws IOException;
}
