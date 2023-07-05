package edu.ncar.cisl.sage.filewalker;

import edu.ncar.cisl.sage.filewalker.impl.DirectoryErrorEventImpl;
import edu.ncar.cisl.sage.filewalker.impl.DirectoryFoundEventImpl;
import edu.ncar.cisl.sage.filewalker.impl.FileErrorEventImpl;
import edu.ncar.cisl.sage.filewalker.impl.FileFoundEventImpl;
import edu.ncar.cisl.sage.identification.IdCalculator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class LoggingFileVisitor implements FileVisitor<Path>, ApplicationEventPublisherAware {

    private long countFile = 0;

    private long countDirectory = 0;
    private long countErrorOther = 0;

    private long countErrorFile = 0;

    private long countErrorDirectory = 0;

    private final List<String> ignoredPaths;

    private final IdCalculator idCalculator;
    private ApplicationEventPublisher applicationEventPublisher;

    public LoggingFileVisitor(List<String> ignoredPaths, IdCalculator idCalculator) {
        this.ignoredPaths = ignoredPaths;
        this.idCalculator = idCalculator;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

        // TODO change to stream.
        if(Files.isSymbolicLink(dir)){
            return SKIP_SUBTREE;
        }
        for(int i = 0; i < this.ignoredPaths.size(); i++){
            if(dir.toString().contains(this.ignoredPaths.get(i))) {
                return SKIP_SUBTREE;
            }
        }
        return CONTINUE;
    }



    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException {

        if (Files.isRegularFile(path)) {
            countFile++;
            //Makes event of type FileFound and publishes it
            this.fireFileFoundEvent(path, attr);
        }
        return CONTINUE;
    }

    private void fireFileFoundEvent(Path path, BasicFileAttributes attr) throws IOException {

        //Create and Populate FileFoundEvent
        FileFoundEventImpl fileFoundEventImpl = new FileFoundEventImpl(this);

        fileFoundEventImpl.setId(calculateId(idCalculator, path.toString()));
        fileFoundEventImpl.setFileIdentifier(attr.fileKey().toString());
        fileFoundEventImpl.setFileName(path.getFileName().toString());
        fileFoundEventImpl.setPath(path);
        fileFoundEventImpl.setSize(Files.size(path));
        fileFoundEventImpl.setDateCreated(attr.creationTime().toInstant().atZone(ZoneId.systemDefault()));
        fileFoundEventImpl.setDateModified(Files.getLastModifiedTime(path).toInstant().atZone(ZoneId.systemDefault()));
        fileFoundEventImpl.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
        fileFoundEventImpl.setOwner(Files.getOwner(path).toString());
        fileFoundEventImpl.setGroup(null);
        fileFoundEventImpl.setPermissions(null);

        //Publishes FileFoundEvent
        this.applicationEventPublisher.publishEvent(fileFoundEventImpl);
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {

        if (Files.isDirectory(dir)){
            countDirectory++;

            this.fireDirectoryFoundEvent(dir);
        }
        return CONTINUE;
    }

    private void fireDirectoryFoundEvent(Path path) throws IOException {

        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

        //Create and Populate DirectoryFoundEvent
        DirectoryFoundEventImpl directoryFoundEventImpl = new DirectoryFoundEventImpl(this);

        directoryFoundEventImpl.setId(calculateId(idCalculator, path.toString()));
        directoryFoundEventImpl.setFileIdentifier(attr.fileKey().toString());
        directoryFoundEventImpl.setFileName(path.getFileName().toString());
        directoryFoundEventImpl.setPath(path);
        directoryFoundEventImpl.setSize(Files.size(path));
        directoryFoundEventImpl.setDateCreated(attr.creationTime().toInstant().atZone(ZoneId.systemDefault()));
        directoryFoundEventImpl.setDateModified(Files.getLastModifiedTime(path).toInstant().atZone(ZoneId.systemDefault()));
        directoryFoundEventImpl.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
        directoryFoundEventImpl.setOwner(Files.getOwner(path).toString());

        //Publishes DirectoryFoundEvent
        this.applicationEventPublisher.publishEvent(directoryFoundEventImpl);

    }

    public FileVisitResult visitFileFailed(Path path, IOException e) {

        if (Files.isDirectory(path)) {
            countErrorDirectory++;
            fireDirectoryErrorEvent(path, e);
        }
        else if (Files.isRegularFile(path)){
            countErrorFile++;
            fireFileErrorEvent(path, e);
        }
        else {
            countErrorOther++;
        }

        return CONTINUE;
    }

    public void fireFileErrorEvent(Path path, IOException e) {

        //Create and Populate FileErrorEvent
        FileErrorEventImpl fileErrorEventImpl = new FileErrorEventImpl(this);

        fileErrorEventImpl.setId(calculateId(idCalculator, path.toString()));
        fileErrorEventImpl.setFileIdentifier(null);
        fileErrorEventImpl.setFileName(path.getFileName().toString());
        fileErrorEventImpl.setPath(path);
        fileErrorEventImpl.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
        fileErrorEventImpl.setErrorMessage(e.getMessage());

        //Publishes FileErrorEvent
        this.applicationEventPublisher.publishEvent(fileErrorEventImpl);

    }

    public void fireDirectoryErrorEvent(Path path, IOException e) {

        //Create and Populate DirectoryErrorEvent
        DirectoryErrorEventImpl directoryErrorEventImpl = new DirectoryErrorEventImpl(this);

        directoryErrorEventImpl.setId(calculateId(idCalculator, path.toString()));
        directoryErrorEventImpl.setFileIdentifier(null);
        directoryErrorEventImpl.setFileName(path.getFileName().toString());
        directoryErrorEventImpl.setPath(path);
        directoryErrorEventImpl.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
        directoryErrorEventImpl.setErrorMessage(e.getMessage());

        //Publishes DirectoryErrorEvent
        this.applicationEventPublisher.publishEvent(directoryErrorEventImpl);
    }

    public void reset() {
        countErrorOther = 0;
        countErrorDirectory = 0;
        countErrorFile = 0;
        countFile = 0;
        countDirectory = 0;
    }

    public long getCountFile() {
        return countFile;
    }

    public long getCountDirectory() {
        return countDirectory;
    }

    public long getCountErrorOther() {
        return countErrorOther;
    }

    public long getCountErrorFile() {
        return countErrorFile;
    }

    public long getCountErrorDirectory() {
        return countErrorDirectory;
    }

    public List<String> getIgnoredPaths() {
        return ignoredPaths;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }

    public String calculateId(IdCalculator idCalculator, String path) {

        return (idCalculator.calculateId(path));
    }
}
