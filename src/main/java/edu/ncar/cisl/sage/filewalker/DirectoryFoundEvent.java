package edu.ncar.cisl.sage.filewalker;

import java.nio.file.Path;
import java.time.ZonedDateTime;

public interface DirectoryFoundEvent {

    String getFileIdentifier();

    String getFileName();

    Path getPath();

    Long getSize();

    ZonedDateTime getDateCreated();

    ZonedDateTime getDateModified();

    ZonedDateTime getDateLastIndexed();

    String getOwner();
}
