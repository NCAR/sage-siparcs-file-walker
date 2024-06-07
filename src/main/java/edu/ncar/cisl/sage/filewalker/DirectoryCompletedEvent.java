package edu.ncar.cisl.sage.filewalker;

import java.nio.file.Path;

public interface DirectoryCompletedEvent {

    String getId();

    Path getDir();

}
