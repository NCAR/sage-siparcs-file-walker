package edu.ncar.cisl.sage.metadata;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.netcdf.NetCDFParser;
import org.apache.tika.parser.hdf.HDFParser;
import org.apache.tika.parser.grib.GribParser;

import javax.measure.format.ParserException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;

public class ScientificFilesMetadataFacade {

    public ScientificFilesMetadataFacade() {}

    public String getMetadata(String filePath, String mediaType, String field) throws NoSuchFileException {

        Parser parser = getParser(mediaType);

        Metadata metadata = new Metadata();

        try (InputStream stream = new FileInputStream(filePath)) {

            parser.parse(stream, new BodyContentHandler(), metadata, new ParseContext());

        } catch (NoSuchFileException e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();
            throw e;

        } catch (Exception e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return metadata.get(field);
    }

    private Parser getParser(String mediaType) {

        if(mediaType.equalsIgnoreCase("application/x-netcdf")) {

            return new NetCDFParser();

        } else if(mediaType.equalsIgnoreCase("application/x-hdf")) {

            return new HDFParser();

        } else if(mediaType.equalsIgnoreCase("application/x-grib")){

            return new GribParser();

        } else {

            throw new IllegalArgumentException("Parser not found for media type: " + mediaType);
        }
    }
}
