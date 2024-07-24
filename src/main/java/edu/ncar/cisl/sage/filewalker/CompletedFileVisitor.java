package edu.ncar.cisl.sage.filewalker;

import edu.ncar.cisl.sage.repository.EsDirectoryStateRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class CompletedFileVisitor implements FileVisitor<Path>, ApplicationEventPublisherAware {

    private final FileVisitor<Path> fileEventsFileVisitor;
    private final String walkerId;
    private final EsDirectoryStateRepository repository;

    private ApplicationEventPublisher applicationEventPublisher;

    public CompletedFileVisitor(FileVisitor<Path> fileEventsFileVisitor, EsDirectoryStateRepository repository, String walkerId){

        this.fileEventsFileVisitor = fileEventsFileVisitor;
        this.repository = repository;
        this.walkerId = walkerId;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        FileVisitResult result = CONTINUE;

        boolean completed = this.repository.isDirectoryCompleted(walkerId,dir);

        if(completed) {
            result = SKIP_SUBTREE;
        } else {
            this.fileEventsFileVisitor.preVisitDirectory(dir,attrs);
        }

        return result;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException {

        return this.fileEventsFileVisitor.visitFile(path,attr);
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {

        fireDirectoryCompletedEvent(dir);
        return this.fileEventsFileVisitor.postVisitDirectory(dir,e);
    }

    public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {

        return this.fileEventsFileVisitor.visitFileFailed(path,e);
    }

    private void fireDirectoryCompletedEvent(Path dir) {

        DirectoryCompletedEvent directoryCompletedEvent = new DirectoryCompletedEvent(this);

        directoryCompletedEvent.setId(walkerId);
        directoryCompletedEvent.setDir(dir);

        this.applicationEventPublisher.publishEvent(directoryCompletedEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}