package edu.ncar.cisl.sage.metadata.mediaType;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public interface MediaTypeStrategy {

    String calculateMetadata(Path path) throws NoSuchFileException;
}
