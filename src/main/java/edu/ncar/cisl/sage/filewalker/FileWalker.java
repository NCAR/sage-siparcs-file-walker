package edu.ncar.cisl.sage.filewalker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Date;

public class FileWalker {

    private final Path path;
    private final LoggingFileVisitor fileVisitor;
    private final Clock clock;

    private Date lastAccess;

    private long duration;

    public FileWalker(Path path, LoggingFileVisitor fileVisitor, Clock clock) {

        this.path = path;
        this.fileVisitor = fileVisitor;
        this.clock = clock;
    }

    public void walkFiles() throws IOException {

        //Time set up for knowing runtime
        long startTime = clock.millis();
        this.lastAccess = Date.from(this.clock.instant());


        Files.walkFileTree(this.path, this.fileVisitor);
        this.duration = clock.millis() - startTime;
    }

    public long getFileCount(){return this.fileVisitor.getCountFile();}

    public long getDirectoryCount(){return this.fileVisitor.getCountDirectory();}

    public long getErrorCount(){return this.fileVisitor.getCountError();}

    public long getDuration(){return duration;}

    public Date getLastAccess(){return lastAccess;}
}
