package edu.ncar.cisl.sage.filewalker;

import java.nio.file.Path;
import java.time.ZonedDateTime;

public interface FileFoundEvent {

    String getFileIdentifier();

    String getFileName();

    Path getPath();

    long getSize();

    ZonedDateTime getDateCreated();

    ZonedDateTime getDateModified();

    ZonedDateTime getDateLastIndexed();

    String getOwner();

    String getGroup();

    String getPermissions();
}
