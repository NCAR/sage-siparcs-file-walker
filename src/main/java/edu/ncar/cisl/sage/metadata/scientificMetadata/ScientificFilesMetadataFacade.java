package edu.ncar.cisl.sage.metadata.scientificMetadata;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.Parser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;

public class ScientificFilesMetadataFacade {

    ParserFactory parserFactory;

    public ScientificFilesMetadataFacade(ParserFactory parserFactory) {

        this.parserFactory = parserFactory;
    }

    public String getMetadata(String filePath, String mediaType, String field) throws NoSuchFileException {

        Parser parser = parserFactory.getParser(mediaType);

        Metadata metadata = parserFactory.getMetadata();

        try (InputStream stream = new FileInputStream(filePath)) {

            parser.parse(stream, parserFactory.getBodyContentHandler(), metadata, parserFactory.getParseContext());

        } catch (NoSuchFileException e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();
            throw e;

        } catch (Exception e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        for(String name : metadata.names()) {

            System.out.println(name + ": " + metadata.get(name));
        }

        return metadata.get(field);
    }
}
