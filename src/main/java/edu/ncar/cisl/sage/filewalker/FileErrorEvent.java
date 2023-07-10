package edu.ncar.cisl.sage.filewalker;

import java.nio.file.Path;
import java.time.ZonedDateTime;

public interface FileErrorEvent {

    String getFileIdentifier();

    String getFileName();

    Path getPath();

    String getExtension();

    ZonedDateTime getDateLastIndexed();

    String getErrorMessage();
}

