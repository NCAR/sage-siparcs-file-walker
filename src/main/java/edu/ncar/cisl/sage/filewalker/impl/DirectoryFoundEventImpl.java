package edu.ncar.cisl.sage.filewalker.impl;

import edu.ncar.cisl.sage.filewalker.DirectoryFoundEvent;
import org.springframework.context.ApplicationEvent;

import java.nio.file.Path;
import java.time.ZonedDateTime;

public class DirectoryFoundEventImpl extends ApplicationEvent implements DirectoryFoundEvent {

    private String fileIdentifier;
    private String fileName;
    private Path path;
    private Long size;
    private ZonedDateTime dateCreated;
    private ZonedDateTime dateModified;
    private ZonedDateTime dateLastIndexed;
    private String owner;

    public DirectoryFoundEventImpl(Object source) {

        super(source);
    }

    @Override
    public String getFileIdentifier() {
        return fileIdentifier;
    }

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public Long getSize() { return size; }

    public void setSize(Long size) {this.size = size; }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ZonedDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(ZonedDateTime dateModified) {
        this.dateModified = dateModified;
    }

    @Override
    public ZonedDateTime getDateLastIndexed() {
        return dateLastIndexed;
    }

    public void setDateLastIndexed(ZonedDateTime dateLastIndexed) {
        this.dateLastIndexed = dateLastIndexed;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
