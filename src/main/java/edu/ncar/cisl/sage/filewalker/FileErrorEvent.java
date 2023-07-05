package edu.ncar.cisl.sage.filewalker;

import java.nio.file.Path;
import java.time.ZonedDateTime;

public interface FileErrorEvent {

    String getId();
    String getFileIdentifier();

    String getFileName();

    Path getPath();

    ZonedDateTime getDateLastIndexed();

    String getErrorMessage();
}

