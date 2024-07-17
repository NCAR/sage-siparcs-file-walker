package edu.ncar.cisl.sage.mediator;

import edu.ncar.cisl.sage.filewalker.DirectoryErrorEvent;
import edu.ncar.cisl.sage.filewalker.DirectoryFoundEvent;
import edu.ncar.cisl.sage.filewalker.FileErrorEvent;
import edu.ncar.cisl.sage.filewalker.FileFoundEvent;
import edu.ncar.cisl.sage.identification.IdStrategy;
import edu.ncar.cisl.sage.model.EsFile;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class WalkerMediator {

    private final EsFileRepository repository;

    private final IdStrategy idStrategy;

    @Autowired
    public WalkerMediator(EsFileRepository repository, IdStrategy idStrategy) {

        this.repository = repository;
        this.idStrategy = idStrategy;
    }

    @EventListener
    public void handleDirectoryFoundEvent(DirectoryFoundEvent event) {

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

        String id = idStrategy.calculateId(event.getPath().toString());

        this.repository.addFile(id, esFile);
    }

    @EventListener
    public void handleDirectoryErrorEvent (DirectoryErrorEvent event) {

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

        String id = idStrategy.calculateId(event.getPath().toString());

        this.repository.addFile(id, esFile);
    }

    @EventListener
    public void handleFileFoundEvent (FileFoundEvent event) {

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

        String id = idStrategy.calculateId(event.getPath().toString());

        this.repository.addFile(id, esFile);
    }

    @EventListener
    public void handleFileErrorEvent (FileErrorEvent event) {

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

        String id = idStrategy.calculateId(event.getPath().toString());

        this.repository.addFile(id, esFile);
    }

    private String reformatDate(ZonedDateTime zonedDateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ");

        return (zonedDateTime.format((formatter)));
    }
}