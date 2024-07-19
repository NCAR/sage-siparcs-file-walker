package edu.ncar.cisl.sage.metadata.scientificMetadata;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;

public class ScientificFilesMetadataFacade {

    private final ParserFactory parserFactory;

    private static final Logger LOG = LoggerFactory.getLogger(ScientificFilesMetadataFacade.class);

    public ScientificFilesMetadataFacade(ParserFactory parserFactory) {

        this.parserFactory = parserFactory;
    }

    public String getMetadata(String filePath, String mediaType, String field) throws NoSuchFileException {

        Parser parser = parserFactory.getParser(mediaType);

        Metadata metadata = parserFactory.getMetadata();

        try (InputStream stream = new FileInputStream(filePath)) {

            parser.parse(stream, parserFactory.getBodyContentHandler(), metadata, parserFactory.getParseContext());

        } catch (NoSuchFileException e) {

            LOG.error(e.getMessage(), e);
            throw e;

        } catch (Exception e) {

            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        for(String name : metadata.names()) {

            System.out.println(name + ": " + metadata.get(name));
        }

        return metadata.get(field);
    }
}
