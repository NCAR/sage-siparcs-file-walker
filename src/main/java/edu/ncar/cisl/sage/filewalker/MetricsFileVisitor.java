package edu.ncar.cisl.sage.filewalker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

public class MetricsFileVisitor implements FileVisitor<Path> {

    private long countFile = 0;
    private long countDirectory = 0;
    private long countErrorOther = 0;
    private long countErrorFile = 0;
    private long countErrorDirectory = 0;

    private final FileVisitor<Path> visitor;

    private static final Logger LOG = LoggerFactory.getLogger(MetricsFileVisitor.class);

    public MetricsFileVisitor(FileVisitor<Path> visitor) {

        this.visitor = visitor;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        return this.visitor.preVisitDirectory(dir, attrs);
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException {

        FileVisitResult result = this.visitor.visitFile(path, attr);
        if (Files.isRegularFile(path) && result == CONTINUE) {
            countFile++;
        }
        return result;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {

        FileVisitResult result = this.visitor.postVisitDirectory(dir, e);
        if (Files.isDirectory(dir) && result == CONTINUE) {
            countDirectory++;
        }
        return result;
    }

    public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {

        if (Files.isDirectory(path)) {
            countErrorDirectory++;
        }
        else if (Files.isRegularFile(path)){
            countErrorFile++;
        }
        else {
            countErrorOther++;
            LOG.error(e.getMessage(), e);
        }
        return visitor.visitFileFailed(path, e);
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
}
