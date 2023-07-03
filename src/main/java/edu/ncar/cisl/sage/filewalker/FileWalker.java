package edu.ncar.cisl.sage.filewalker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Date;
import java.util.List;

public class FileWalker {

    private final Path path;
    private final LoggingFileVisitor fileVisitor;
    private final Clock clock;

    private boolean Running = false;

    private Date lastAccess;

    private long duration = 0;

    private long startTime;

    private boolean isFinished = true;

    public FileWalker(Path path, LoggingFileVisitor fileVisitor, Clock clock) {

        this.path = path;
        this.fileVisitor = fileVisitor;
        this.clock = clock;
    }

    public void walkFiles() throws IOException {

        this.Running = true;
        this.isFinished = false;
        this.fileVisitor.reset();

        try {

            //Time set up for knowing runtime
            this.startTime = clock.millis();
            this.lastAccess = Date.from(this.clock.instant());

            Files.walkFileTree(this.path, this.fileVisitor);
            this.duration = clock.millis() - startTime;

        } finally {
            this.Running = false;
            this.isFinished = true;
        }
    }

    public long getFileCount() {
        return this.fileVisitor.getCountFile();
    }

    public long getDirectoryCount() {
        return this.fileVisitor.getCountDirectory();
    }

    public long getDirectoryErrorCount() {
        return this.fileVisitor.getCountErrorDirectory();
    }

    public long getOtherErrorCount() {
        return this.fileVisitor.getCountErrorOther();
    }

    public long getFileErrorCount() {
        return this.fileVisitor.getCountErrorFile();
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

    public List<String> getIgnoredPaths(){
        return this.fileVisitor.getIgnoredPaths();
    }

    public boolean isRunning() {
        return this.Running;
    }
}
