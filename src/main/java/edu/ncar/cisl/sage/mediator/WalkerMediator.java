package edu.ncar.cisl.sage.mediator;

import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import edu.ncar.cisl.sage.filewalker.impl.DirectoryErrorEventImpl;
import edu.ncar.cisl.sage.filewalker.impl.DirectoryFoundEventImpl;
import edu.ncar.cisl.sage.filewalker.impl.FileErrorEventImpl;
import edu.ncar.cisl.sage.filewalker.impl.FileFoundEventImpl;
import edu.ncar.cisl.sage.identification.IdStrategy;
import edu.ncar.cisl.sage.model.EsFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static edu.ncar.cisl.sage.WorkingFileVisitorApplication.esIndex;


@Component
public class WalkerMediator {

    private final BulkIngester<Void> ingester;

    private final IdStrategy idStrategy;

    @Autowired
    public WalkerMediator(BulkIngester<Void> ingester, IdStrategy idStrategy) {

        this.ingester = ingester;
        this.idStrategy = idStrategy;
    }

    @EventListener
    public void handleDirectoryFoundEvent(DirectoryFoundEventImpl event) {

        //Create and Populate EsFile
        EsFile esFile = new EsFile();

        esFile.setFileIdentifier(event.getFileIdentifier());
        esFile.setFileName(event.getFileName());
        esFile.setPath(event.getPath());
        esFile.setDirectory(true);
        esFile.setDateCreated(reformatDate(event.getDateCreated()));
        esFile.setDateModified(reformatDate(event.getDateModified()));
        esFile.setDateLastIndexed(reformatDate(event.getDateLastIndexed()));
        esFile.setOwner(event.getOwner());
        esFile.setError(false);
        esFile.setMissing(false);

        // Ship to ES.
        ingester.add(op -> op
                .index(idx -> idx
                        .index(esIndex)
                        .document(esFile)
                        .id(idStrategy.calculateId(event.getPath().toString()))
                )
        );

    }

    @EventListener
    public void handleDirectoryErrorEvent (DirectoryErrorEventImpl event) {

        //Create and Populate EsFile
        EsFile esFile = new EsFile();

        esFile.setFileIdentifier(event.getFileIdentifier());
        esFile.setFileName(event.getFileName());
        esFile.setPath(event.getPath());
        esFile.setDirectory(true);
        esFile.setDateLastIndexed(reformatDate(event.getDateLastIndexed()));
        esFile.setError(true);
        esFile.setErrorMessage(event.getErrorMessage());
        esFile.setMissing(false);

        // Ship to ES.
        ingester.add(op -> op
                .index(idx -> idx
                        .index(esIndex)
                        .document(esFile)
                        .id(idStrategy.calculateId(event.getPath().toString()))
                )
        );
    }

    @EventListener
    public void handleFileFoundEvent (FileFoundEventImpl event) {

        //Create and Populate EsFile
        EsFile esFile = new EsFile();

        esFile.setFileIdentifier(event.getFileIdentifier());
        esFile.setFileName(event.getFileName());
        esFile.setPath(event.getPath());
        esFile.setExtension(event.getExtension());
        esFile.setDirectory(false);
        esFile.setSize(event.getSize());
        esFile.setDateCreated(reformatDate(event.getDateCreated()));
        esFile.setDateModified(reformatDate(event.getDateModified()));
        esFile.setDateLastIndexed(reformatDate(event.getDateLastIndexed()));
        esFile.setOwner(event.getOwner());
        esFile.setGroup(event.getGroup());
        esFile.setPermissions(event.getPermissions());
        esFile.setError(false);
        esFile.setMissing(false);

        // Ship to ES.
        ingester.add(op -> op
                .index(idx -> idx
                        .index(esIndex)
                        .document(esFile)
                        .id(idStrategy.calculateId(event.getPath().toString()))
                )
        );
    }

    @EventListener
    public void handleFileErrorEvent (FileErrorEventImpl event) {

        //Create and Populate EsFile
        EsFile esFile = new EsFile();

        esFile.setFileIdentifier(event.getFileIdentifier());
        esFile.setFileName(event.getFileName());
        esFile.setPath(event.getPath());
        esFile.setExtension(event.getExtension());
        esFile.setDirectory(false);
        esFile.setDateLastIndexed(reformatDate(event.getDateLastIndexed()));
        esFile.setError(true);
        esFile.setErrorMessage(event.getErrorMessage());
        esFile.setMissing(false);

        // Ship to ES.
        ingester.add(op -> op
                .index(idx -> idx
                        .index(esIndex)
                        .document(esFile)
                        .id(idStrategy.calculateId(event.getPath().toString()))
                )
        );
    }

    private String reformatDate(ZonedDateTime zonedDateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ");

        return (zonedDateTime.format((formatter)));
    }
}