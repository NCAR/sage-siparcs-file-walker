package edu.ncar.cisl.sage.model;

import java.nio.file.Path;
import java.time.ZonedDateTime;

public class EsFile {

    private String fileIdentifier;
    private String fileName;
    private Path path;
    private Boolean isDirectory;
    private Long size;
    private ZonedDateTime dateCreated;
    private ZonedDateTime dateModified;
    private ZonedDateTime dateLastIndexed;
    private String owner;
    private String group; //Adjust if it is an id
    private String permissions; //Files has isReadable(Path path), isExecutable(Path path), and isWritable(Path path) as returning boolean

    private Boolean isError;

    private String errorMessage;

    public String getFileIdentifier() {
        return this.fileIdentifier;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Path getPath() {
        return this.path;
    }

    public Boolean isDirectory() {
        return this.isDirectory;
    }

    public Long getSize() {
        return this.size;
    }

    public ZonedDateTime getDateCreated() {
        return this.dateCreated;
    }

    public ZonedDateTime getDateModified() {
        return this.dateModified;
    }

    public ZonedDateTime getDateLastIndexed() {
        return this.dateLastIndexed;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getGroup() {
        return this.group;
    }

    public String getPermissions() {
        return this.permissions;
    }

    //Setters

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setDirectory(Boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {

            this.dateCreated = dateCreated;
    }

    public void setDateModified(ZonedDateTime dateModified) {

        this.dateModified = dateModified;
    }

    public void setDateLastIndexed(ZonedDateTime dateLastIndexed) {

            this.dateLastIndexed = dateLastIndexed;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Boolean getError() {
        return isError;
    }

    public void setError(Boolean error) {
        isError = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
