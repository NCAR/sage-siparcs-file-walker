package edu.ncar.cisl.sage.metadata.scientificMetadata.impl;

import edu.ncar.cisl.sage.metadata.scientificMetadata.ParserFactory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.netcdf.NetCDFParser;
import org.apache.tika.parser.hdf.HDFParser;
import org.apache.tika.parser.grib.GribParser;

public class ParserFactoryImpl implements ParserFactory {

    @Override
    public Parser getParser(String mediaType) {

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

    @Override
    public Metadata getMetadata() {

        return new Metadata();
    }

    @Override
    public BodyContentHandler getBodyContentHandler() {

        return new BodyContentHandler();
    }

    @Override
    public ParseContext getParseContext() {

        return new ParseContext();
    }
}
