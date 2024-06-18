package edu.ncar.cisl.sage.model;

import java.nio.file.Path;
import java.util.Set;

public class EsDirectoryState {

    private String id;
    private Set<Path> completed;
    private String dateCreated;
    private String dateUpdated;

    public EsDirectoryState(){}

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Set<Path> getCompleted() { return completed; }

    public void setCompleted(Set<Path> completed) { this.completed = completed; }

    public String getDateCreated() { return dateCreated; }

    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    public String getDateUpdated() { return dateUpdated; }

    public void setDateUpdated(String dateUpdated) { this.dateUpdated = dateUpdated; }
}
