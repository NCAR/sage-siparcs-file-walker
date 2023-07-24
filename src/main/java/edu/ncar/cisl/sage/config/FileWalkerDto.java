package edu.ncar.cisl.sage.config;

import java.util.List;

public class FileWalkerDto {

    private String id;
    private String startPath;
    private List<String> ignoredPaths;

    public FileWalkerDto() {
    }

    public FileWalkerDto(String id, String startPath, List<String> ignoredPaths) {
        this.id = id;
        this.startPath = startPath;
        this.ignoredPaths = ignoredPaths;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartPath() {
        return startPath;
    }

    public void setStartPath(String startPath) {
        this.startPath = startPath;
    }

    public List<String> getIgnoredPaths() {
        return ignoredPaths;
    }

    public void setIgnoredPaths(List<String> ignoredPaths) {
        this.ignoredPaths = ignoredPaths;
    }
}
