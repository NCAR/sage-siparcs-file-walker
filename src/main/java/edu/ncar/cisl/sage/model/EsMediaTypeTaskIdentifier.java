package edu.ncar.cisl.sage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.nio.file.Path;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EsMediaTypeTaskIdentifier {

    private String id;
    private Path path;

    public EsMediaTypeTaskIdentifier() {}

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Path getPath() { return path; }

    public void setPath(Path path) { this.path = path; }
}
