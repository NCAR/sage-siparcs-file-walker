package edu.ncar.cisl.sage.filewalker.impl;

import edu.ncar.cisl.sage.filewalker.DirectoryErrorEvent;
import org.springframework.context.ApplicationEvent;

import java.nio.file.Path;
import java.time.ZonedDateTime;

public class DirectoryErrorEventImpl extends ApplicationEvent implements DirectoryErrorEvent {

    private String id;
    private String fileIdentifier;
    private String fileName;
    private Path path;
    private ZonedDateTime dateLastIndexed;
    private String errorMessage;

    public DirectoryErrorEventImpl(Object source) {

        super(source);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public ZonedDateTime getDateLastIndexed() {
        return dateLastIndexed;
    }

    public void setDateLastIndexed(ZonedDateTime dateLastIndexed) {
        this.dateLastIndexed = dateLastIndexed;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
