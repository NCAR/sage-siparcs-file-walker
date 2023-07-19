package edu.ncar.cisl.sage.filewalker;

import edu.ncar.cisl.sage.filewalker.impl.*;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.io.*;
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

    private ApplicationEventPublisher applicationEventPublisher;

    public LoggingFileVisitor(List<String> ignoredPaths) {
        this.ignoredPaths = ignoredPaths;
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

        fileFoundEventImpl.setFileIdentifier(attr.fileKey().toString());
        fileFoundEventImpl.setFileName(path.getFileName().toString());
        fileFoundEventImpl.setPath(path);
        fileFoundEventImpl.setExtension(getExtension(path.getFileName().toString()));
//        fileFoundEventImpl.setType(getType(path));
        fileFoundEventImpl.setSize(Files.size(path));
        fileFoundEventImpl.setDateCreated(attr.creationTime().toInstant().atZone(ZoneId.systemDefault()));
        fileFoundEventImpl.setDateModified(Files.getLastModifiedTime(path).toInstant().atZone(ZoneId.systemDefault()));
        fileFoundEventImpl.setDateLastIndexed(ZonedDateTime.now(ZoneId.systemDefault()));
        fileFoundEventImpl.setOwner(Files.getOwner(path).toString());

        //Publishes FileFoundEvent
        this.applicationEventPublisher.publishEvent(fileFoundEventImpl);
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

//    public String getType(Path path) throws IOException {
//
//        InputStream stream = null;
//        try {
//            stream = Files.newInputStream(path);
//        } catch (IOException e) {
//            //System.out.println(e.toString());
//            throw new RuntimeException(e);
//        }
//
//        Tika tika = new Tika();
//
//        String mediaType = tika.detect(stream);
//
//        return Objects.requireNonNullElse(mediaType, "No MediaType Calculated");
//    }

    public String getType(Path path) throws IOException {

//        InputStream is = null;
//
//        try {
//
//            is = Files.newInputStream(path);
//
//        } catch (Exception e) {
//
//        }
//        finally {
//
//            if (is != null) {
//
//                try {
//                    is.close();
//                } catch (Exception e) {
//
//                }
//
//            }
//        }

        //This ONE
//        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(path))) {
//
//            Detector detector = new DefaultDetector();
//            Metadata metadata = new Metadata();
//
//            MediaType mediaType = detector.detect(inputStream, metadata);
//
//            //System.out.println(String.format("%s %s", path, mediaType));
//
//        } catch (Exception e) {
//
//            System.out.println("Exception: " + e);
//            e.printStackTrace();
//        }

//        FileInputStream input = new FileInputStream(path.toFile());
//        InputStream input = null;
//        try {
//            input = Files.newInputStream(path);
//        } finally {
//            if (input.equals(null)) {
//                return "No Stream Value Calculated";
//            }
//        }

//        System.out.println(input);

//        Detector detector = new DefaultDetector();
//        Metadata metadata = new Metadata();
//
//        MediaType mediaType = detector.detect(input, metadata);
//
//        return Objects.requireNonNullElse(mediaType.toString(), "No MediaType Calculated");

        return null;
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
           // System.out.println(e.toString());
        }

        return CONTINUE;
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
}
