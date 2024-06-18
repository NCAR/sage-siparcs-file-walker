package edu.ncar.cisl.sage.metadata;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public interface MetadataStrategy {

    String calculateMetadata(Path path) throws NoSuchFileException;
}
