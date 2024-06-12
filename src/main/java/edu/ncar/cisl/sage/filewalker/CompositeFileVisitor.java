package edu.ncar.cisl.sage.filewalker;

import edu.ncar.cisl.sage.filewalker.impl.DirectoryCompletedEventImpl;
import edu.ncar.cisl.sage.filewalker.impl.FileWalkerCompletedEventImpl;
import edu.ncar.cisl.sage.repository.EsDirStateRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class CompositeFileVisitor implements FileVisitor<Path>, ApplicationEventPublisherAware {

    private final FileVisitor<Path> fileEventsFileVisitor;
    private final String walkerId;
    private final String startingPath;
    private EsDirStateRepository repository;

    private ApplicationEventPublisher applicationEventPublisher;

    public CompositeFileVisitor(FileVisitor<Path> fileEventsFileVisitor, EsDirStateRepository repository, String walkerId, String startingPath){

        this.fileEventsFileVisitor = fileEventsFileVisitor;
        this.repository = repository;
        this.walkerId = walkerId;
        this.startingPath = startingPath;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        FileVisitResult result = CONTINUE;

        boolean completed = this.repository.isCompleted(walkerId,dir);

        if(completed) {
            System.out.println(dir + "   skip");
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

        System.out.println(dir + "   post");
        if(dir.equals(Path.of(this.startingPath))) {
            fireFileWalkerCompletedEvent();
        } else {
            fireDirectoryCompletedEvent(dir, Path.of(this.startingPath));
        }
        return this.fileEventsFileVisitor.postVisitDirectory(dir,e);
    }

    public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {

        return this.fileEventsFileVisitor.visitFileFailed(path,e);
    }

    private void fireDirectoryCompletedEvent(Path dir, Path startingPath) {

        DirectoryCompletedEventImpl dirCompletedEventImpl = new DirectoryCompletedEventImpl(this);

        dirCompletedEventImpl.setId(walkerId);
        dirCompletedEventImpl.setDir(dir);
        dirCompletedEventImpl.setStartingPath(startingPath);

        this.applicationEventPublisher.publishEvent(dirCompletedEventImpl);
    }

    private void fireFileWalkerCompletedEvent() {

        FileWalkerCompletedEventImpl fileWalkerCompletedEventImpl = new FileWalkerCompletedEventImpl(this);
        fileWalkerCompletedEventImpl.setId(walkerId);
        this.applicationEventPublisher.publishEvent(fileWalkerCompletedEventImpl);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}