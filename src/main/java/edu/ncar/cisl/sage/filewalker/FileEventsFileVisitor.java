package edu.ncar.cisl.sage.filewalker;

import edu.ncar.cisl.sage.filewalker.impl.DirectoryErrorEventImpl;
import edu.ncar.cisl.sage.filewalker.impl.DirectoryFoundEventImpl;
import edu.ncar.cisl.sage.filewalker.impl.FileErrorEventImpl;
import edu.ncar.cisl.sage.filewalker.impl.FileFoundEventImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;

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
            FileFoundEventImpl fileFoundEventImpl = new FileFoundEventImpl(this);

            fileFoundEventImpl.setFileIdentifier(attr.fileKey().toString());
            fileFoundEventImpl.setFileName(path.getFileName().toString());
            fileFoundEventImpl.setPath(path);
            fileFoundEventImpl.setExtension(getExtension(path.getFileName().toString()));
            fileFoundEventImpl.setSize(Files.size(path));
            fileFoundEventImpl.setDateCreated(attr.creationTime().toInstant().atZone(ZoneId.systemDefault()));
            fileFoundEventImpl.setDateModified(Files.getLastModifiedTime(path).toInstant().atZone(ZoneId.systemDefault()));
            fileFoundEventImpl.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
            fileFoundEventImpl.setOwner(Files.getOwner(path).toString());

            //Publishes FileFoundEvent
            this.applicationEventPublisher.publishEvent(fileFoundEventImpl);

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
        DirectoryFoundEventImpl directoryFoundEventImpl = new DirectoryFoundEventImpl(this);

        directoryFoundEventImpl.setFileIdentifier(attr.fileKey().toString());
        directoryFoundEventImpl.setFileName(path.getFileName().toString());
        directoryFoundEventImpl.setPath(path);
        directoryFoundEventImpl.setDateCreated(attr.creationTime().toInstant().atZone(ZoneId.systemDefault()));
        directoryFoundEventImpl.setDateModified(Files.getLastModifiedTime(path).toInstant().atZone(ZoneId.systemDefault()));
        directoryFoundEventImpl.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
        directoryFoundEventImpl.setOwner(Files.getOwner(path).toString());

        //Publishes DirectoryFoundEvent
        this.applicationEventPublisher.publishEvent(directoryFoundEventImpl);

    }

    public void fireFileErrorEvent(Path path, IOException e) {

        //Create and Populate FileErrorEvent
        FileErrorEventImpl fileErrorEventImpl = new FileErrorEventImpl(this);

        fileErrorEventImpl.setFileIdentifier(null);
        fileErrorEventImpl.setFileName(path.getFileName().toString());
        fileErrorEventImpl.setPath(path);
        fileErrorEventImpl.setExtension(getExtension(path.getFileName().toString()));
        fileErrorEventImpl.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
        //getMessage() was only returning the path and did not include the error message
        //The reason for this is unclear. Thus, toString() is being used instead as it is an acceptable message.
        fileErrorEventImpl.setErrorMessage(e.toString());

        //Publishes FileErrorEvent
        this.applicationEventPublisher.publishEvent(fileErrorEventImpl);

    }

    public void fireDirectoryErrorEvent(Path path, IOException e) {

        //Create and Populate DirectoryErrorEvent
        DirectoryErrorEventImpl directoryErrorEventImpl = new DirectoryErrorEventImpl(this);

        directoryErrorEventImpl.setFileIdentifier(null);
        directoryErrorEventImpl.setFileName(path.getFileName().toString());
        directoryErrorEventImpl.setPath(path);
        directoryErrorEventImpl.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
        //getMessage() was only returning the path and did not include the error message
        //The reason for this is unclear. Thus, toString() is being used instead as it is an acceptable message.
        directoryErrorEventImpl.setErrorMessage(e.toString());

        //Publishes DirectoryErrorEvent
        this.applicationEventPublisher.publishEvent(directoryErrorEventImpl);
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
