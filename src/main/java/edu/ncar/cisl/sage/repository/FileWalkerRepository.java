package edu.ncar.cisl.sage.repository;

import edu.ncar.cisl.sage.filewalker.FileWalker;

import java.util.Collection;
import java.util.Map;

public class FileWalkerRepository {

    private Map<String, FileWalker> fileWalkerMap;

    public FileWalkerRepository(Map<String, FileWalker> fileWalkerMap) {
        this.fileWalkerMap = fileWalkerMap;
    }

    public Collection<FileWalker> getAll() {

        return fileWalkerMap.values();
    }
}
