package edu.ncar.cisl.sage.metadata.scientificMetadata;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ScientificFilesMetadataFacade {

    private final ParserFactory parserFactory;

    private static final Logger LOG = LoggerFactory.getLogger(ScientificFilesMetadataFacade.class);
    private static final Logger SM_LOG = LoggerFactory.getLogger("scientific-metadata");

    public ScientificFilesMetadataFacade(ParserFactory parserFactory) {

        this.parserFactory = parserFactory;
    }

    public String getMetadata(String filePath, String mediaType, String field) throws NoSuchFileException {

        Parser parser = parserFactory.getParser(mediaType);

        Metadata metadata = parserFactory.getMetadata();

        try (InputStream stream = new FileInputStream(filePath)) {

            parser.parse(stream, parserFactory.getBodyContentHandler(), metadata, parserFactory.getParseContext());

            if (SM_LOG.isDebugEnabled()) {

                SM_LOG.debug(String.format("%s metadata: %s", filePath, Arrays.stream(metadata.names())
                        .map(name -> String.format("%s: %s", name, metadata.get(name)))
                        .collect(Collectors.joining("\n"))));
            }

        } catch (NoSuchFileException e) {

            LOG.error(e.getMessage(), e);
            throw e;

        } catch (Exception e) {

            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return metadata.get(field);
    }
}
