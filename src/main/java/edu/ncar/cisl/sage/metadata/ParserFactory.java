package edu.ncar.cisl.sage.metadata;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

public interface ParserFactory {

    public Parser getParser(String mediaType);

    public Metadata getMetadata();

    public BodyContentHandler getBodyContentHandler();

    public ParseContext getParseContext();
}
