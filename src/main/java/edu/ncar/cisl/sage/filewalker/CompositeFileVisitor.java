package edu.ncar.cisl.sage.filewalker;

import edu.ncar.cisl.sage.filewalker.impl.DirectoryCompletedEventImpl;
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
    private ApplicationEventPublisher applicationEventPublisher;
    private EsDirStateRepository repository;

    public CompositeFileVisitor(FileVisitor<Path> fileEventsFileVisitor, EsDirStateRepository repository, String walkerId){

        this.fileEventsFileVisitor = fileEventsFileVisitor;
        this.walkerId = walkerId;
        this.repository = repository;
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
        fireDirCompletedEvent(dir);
        return this.fileEventsFileVisitor.postVisitDirectory(dir,e);
    }

    public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {

        return this.fileEventsFileVisitor.visitFileFailed(path,e);
    }

    private void fireDirCompletedEvent(Path dir) {

        DirectoryCompletedEventImpl dirCompletedEventImpl = new DirectoryCompletedEventImpl(this);

        dirCompletedEventImpl.setId(walkerId);
        dirCompletedEventImpl.setDir(dir);

        this.applicationEventPublisher.publishEvent(dirCompletedEventImpl);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}