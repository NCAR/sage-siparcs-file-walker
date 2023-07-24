package edu.ncar.cisl.sage.metadata;

import edu.ncar.cisl.sage.model.EsFile;

import java.nio.file.NoSuchFileException;

public interface MetadataStrategy {

    void calculateMetadata(EsFile esFile) throws NoSuchFileException;
}
