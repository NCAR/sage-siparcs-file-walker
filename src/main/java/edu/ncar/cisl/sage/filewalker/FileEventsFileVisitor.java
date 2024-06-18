package edu.ncar.cisl.sage.filewalker;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FileEventsFileVisitor implements FileVisitor<Path>, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    public FileEventsFileVisitor() {}

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

        return  CONTINUE;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException {

        this.fireFileFoundEvent(path, attr);
        return CONTINUE;
    }

    private void fireFileFoundEvent(Path path, BasicFileAttributes attr) throws IOException {

        try {
            //Create and Populate FileFoundEvent
            FileFoundEvent fileFoundEvent = new FileFoundEvent(this);

            fileFoundEvent.setFileIdentifier(attr.fileKey().toString());
            fileFoundEvent.setFileName(path.getFileName().toString());
            fileFoundEvent.setPath(path);
            fileFoundEvent.setExtension(getExtension(path.getFileName().toString()));
            fileFoundEvent.setSize(Files.size(path));
            fileFoundEvent.setDateCreated(attr.creationTime().toInstant().atZone(ZoneId.systemDefault()));
            fileFoundEvent.setDateModified(Files.getLastModifiedTime(path).toInstant().atZone(ZoneId.systemDefault()));
            fileFoundEvent.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
            fileFoundEvent.setOwner(Files.getOwner(path).toString());

            //Publishes FileFoundEvent
            this.applicationEventPublisher.publishEvent(fileFoundEvent);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getExtension(String pathName) {

        int index = pathName.lastIndexOf('.');

        if  (index > 0) {
            return pathName.substring(index + 1);
        }
        else {
            return null;
        }
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {

        this.fireDirectoryFoundEvent(dir);
        return CONTINUE;
    }

    private void fireDirectoryFoundEvent(Path path) throws IOException {

        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

        //Create and Populate DirectoryFoundEvent
        DirectoryFoundEvent directoryFoundEvent = new DirectoryFoundEvent(this);

        directoryFoundEvent.setFileIdentifier(attr.fileKey().toString());
        directoryFoundEvent.setFileName(path.getFileName().toString());
        directoryFoundEvent.setPath(path);
        directoryFoundEvent.setDateCreated(attr.creationTime().toInstant().atZone(ZoneId.systemDefault()));
        directoryFoundEvent.setDateModified(Files.getLastModifiedTime(path).toInstant().atZone(ZoneId.systemDefault()));
        directoryFoundEvent.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
        directoryFoundEvent.setOwner(Files.getOwner(path).toString());

        //Publishes DirectoryFoundEvent
        this.applicationEventPublisher.publishEvent(directoryFoundEvent);

    }

    public void fireFileErrorEvent(Path path, IOException e) {

        //Create and Populate FileErrorEvent
        FileErrorEvent fileErrorEvent = new FileErrorEvent(this);

        fileErrorEvent.setFileIdentifier(null);
        fileErrorEvent.setFileName(path.getFileName().toString());
        fileErrorEvent.setPath(path);
        fileErrorEvent.setExtension(getExtension(path.getFileName().toString()));
        fileErrorEvent.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
        //getMessage() was only returning the path and did not include the error message
        //The reason for this is unclear. Thus, toString() is being used instead as it is an acceptable message.
        fileErrorEvent.setErrorMessage(e.toString());

        //Publishes FileErrorEvent
        this.applicationEventPublisher.publishEvent(fileErrorEvent);

    }

    public void fireDirectoryErrorEvent(Path path, IOException e) {

        //Create and Populate DirectoryErrorEvent
        DirectoryErrorEvent directoryErrorEvent = new DirectoryErrorEvent(this);

        directoryErrorEvent.setFileIdentifier(null);
        directoryErrorEvent.setFileName(path.getFileName().toString());
        directoryErrorEvent.setPath(path);
        directoryErrorEvent.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
        //getMessage() was only returning the path and did not include the error message
        //The reason for this is unclear. Thus, toString() is being used instead as it is an acceptable message.
        directoryErrorEvent.setErrorMessage(e.toString());

        //Publishes DirectoryErrorEvent
        this.applicationEventPublisher.publishEvent(directoryErrorEvent);
    }

    public FileVisitResult visitFileFailed(Path path, IOException e) {

        if (Files.isDirectory(path)) {
            fireDirectoryErrorEvent(path, e);
        }
        if (Files.isRegularFile(path)){
            fireFileErrorEvent(path, e);
        }
        return CONTINUE;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
