package edu.ncar.cisl.sage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.nio.file.Path;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EsScientificMetadataTaskIdentifier {

    private String id;
    private Path path;
    private String mediaType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
