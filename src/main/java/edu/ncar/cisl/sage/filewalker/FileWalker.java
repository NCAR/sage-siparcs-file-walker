package edu.ncar.cisl.sage.filewalker;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Date;

public class FileWalker implements ApplicationEventPublisherAware {

    private final Path path;
    private final FileVisitor<Path> fileVisitor;
    private final MetricsFileVisitor metricsFileVisitor;
    private final Clock clock;
    private final String walkerId;

    private boolean Running = false;

    private Date lastAccess;

    private long duration = 0;

    private long startTime;

    private boolean isFinished = true;

    private ApplicationEventPublisher applicationEventPublisher;

    public FileWalker(Path path, FileVisitor<Path> fileVisitor, MetricsFileVisitor metricsFileVisitor, Clock clock, String walkerId) {

        this.path = path;
        this.fileVisitor = fileVisitor;
        this.metricsFileVisitor = metricsFileVisitor;
        this.clock = clock;
        this.walkerId = walkerId;
    }

    public void walkFiles() throws IOException {

        this.Running = true;
        this.isFinished = false;
        this.metricsFileVisitor.reset();

        try {

            //Time set up for knowing runtime
            this.startTime = clock.millis();
            this.lastAccess = Date.from(this.clock.instant());

            Files.walkFileTree(this.path, this.fileVisitor);
            fireFileWalkerCompletedEvent();

            this.duration = clock.millis() - startTime;

        } finally {
            this.Running = false;
            this.isFinished = true;
        }
    }

    private void fireFileWalkerCompletedEvent() {

        FileWalkerCompletedEvent fileWalkerCompletedEvent = new FileWalkerCompletedEvent(this);
        fileWalkerCompletedEvent.setId(walkerId);
        this.applicationEventPublisher.publishEvent(fileWalkerCompletedEvent);
    }

    public long getFileCount() {
        return this.metricsFileVisitor.getCountFile();
    }

    public long getDirectoryCount() {
        return this.metricsFileVisitor.getCountDirectory();
    }

    public long getDirectoryErrorCount() {
        return this.metricsFileVisitor.getCountErrorDirectory();
    }

    public long getOtherErrorCount() {
        return this.metricsFileVisitor.getCountErrorOther();
    }

    public long getFileErrorCount() {
        return this.metricsFileVisitor.getCountErrorFile();
    }

    public long getDuration() {

        if (!isFinished) {
            this.duration = clock.millis() - startTime;
        }
        return duration;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public Path getStartingPath() {
        return path;
    }

    public boolean isRunning() {
        return this.Running;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
