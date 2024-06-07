package edu.ncar.cisl.sage.model;

import java.nio.file.Path;
import java.util.Set;

public class EsDirState {

    private String id;
    private Set<Path> completed;
    private String dateStarted;
    private String dateUpdated;

    public EsDirState(){}

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Set<Path> getCompleted() { return completed; }

    public void setCompleted(Set<Path> completed) { this.completed = completed; }

    public String getDateStarted() { return dateStarted; }

    public void setDateStarted(String dateStarted) { this.dateStarted = dateStarted; }

    public String getDateUpdated() { return dateUpdated; }

    public void setDateUpdated(String dateUpdated) { this.dateUpdated = dateUpdated; }
}
