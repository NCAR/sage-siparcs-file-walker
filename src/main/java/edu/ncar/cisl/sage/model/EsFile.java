package edu.ncar.cisl.sage.model;

import java.nio.file.Path;

public class EsFile {

    private String fileIdentifier;
    private String fileName;
    private Path path;
    private String extension;
    private String mediaType;
    private String dateMediaTypeUpdated;
    private Boolean isDirectory;
    private Long size;
    private String dateCreated;
    private String dateModified;
    private String dateLastIndexed;
    private String owner;
    private String group; //Adjust if it is an id
    private String permissions; //Files has isReadable(Path path), isExecutable(Path path), and isWritable(Path path) as returning boolean

    private Boolean isError;

    private String errorMessage;

    private Boolean isMissing;

    private String dateMissing;

    public EsFile() {
    }

    public String getFileIdentifier() {
        return fileIdentifier;
    }

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Boolean getDirectory() {
        return isDirectory;
    }

    public String getDateMediaTypeUpdated() { return this.dateMediaTypeUpdated; }

    public void setDateMediaTypeUpdated(String dateMediaTypeUpdated) { this.dateMediaTypeUpdated = dateMediaTypeUpdated; }

    public void setDirectory(Boolean directory) {
        isDirectory = directory;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getDateLastIndexed() {
        return dateLastIndexed;
    }

    public void setDateLastIndexed(String dateLastIndexed) {
        this.dateLastIndexed = dateLastIndexed;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPermissions() {
        return permissions;
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

    public Boolean getMissing() {
        return isMissing;
    }

    public void setMissing(Boolean missing) {
        isMissing = missing;
    }

    public String getDateMissing() {
        return dateMissing;
    }

    public void setDateMissing(String dateMissing) {
        this.dateMissing = dateMissing;
    }
}
