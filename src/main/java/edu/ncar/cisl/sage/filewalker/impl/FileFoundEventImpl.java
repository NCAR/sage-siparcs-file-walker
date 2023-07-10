package edu.ncar.cisl.sage.filewalker.impl;

import edu.ncar.cisl.sage.filewalker.FileFoundEvent;
import org.springframework.context.ApplicationEvent;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class FileFoundEventImpl extends ApplicationEvent implements FileFoundEvent {

    private String fileIdentifier;
    private String fileName;
    private Path path;
    private String extension;
    private long size;
    private ZonedDateTime dateCreated;
    private ZonedDateTime dateModified;
    private ZonedDateTime dateLastIndexed;
    private String owner;
    private String group;
    private String permissions;

    public FileFoundEventImpl(Object source) {

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
    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public ZonedDateTime getDateCreated() {

        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
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

    @Override
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
