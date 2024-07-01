package edu.ncar.cisl.sage.metadata.impl;

import edu.ncar.cisl.sage.metadata.MetadataStrategy;
import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class MediaTypeSingleTikaMetadataStrategyImpl implements MetadataStrategy {

    private final Tika tika;

    public MediaTypeSingleTikaMetadataStrategyImpl(Tika tika) {

        this.tika = tika;
    }

    @Override
    public String calculateMetadata(Path path) {

        String value = MediaType.OCTET_STREAM.toString();

        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(path))) {

            if (Files.notExists(path)) {

                System.out.println("Does not exist: " + path);
            }
            value = this.tika.detect(inputStream, path.getFileName().toString());

        } catch (NoSuchFileException e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();

        } catch (IOException e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

        return value;
    }
}
