package edu.ncar.cisl.sage.filewalker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class MetricsFileVisitor implements FileVisitor<Path> {

    private long countFile = 0;
    private long countDirectory = 0;
    private long countErrorOther = 0;
    private long countErrorFile = 0;
    private long countErrorDirectory = 0;

    private final List<String> ignoredPaths;
    private final CompositeFileVisitor visitor;

    private static final Logger LOG = LoggerFactory.getLogger(MetricsFileVisitor.class);

    public MetricsFileVisitor(CompositeFileVisitor compositeFileVisitor, List<String> ignoredPaths) {

        this.visitor = compositeFileVisitor;
        this.ignoredPaths = ignoredPaths;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        FileVisitResult result = visitor.preVisitDirectory(dir, attrs);
        if(Files.isSymbolicLink(dir)){
            result = SKIP_SUBTREE;
        }
        boolean ignored = ignoredPaths.stream()
                .filter(path -> dir.toString().contains(path))
                .anyMatch(m -> true);
        if(ignored){
            result = SKIP_SUBTREE;
        }
        return result;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException {

        FileVisitResult result = visitor.visitFile(path, attr);
        if (Files.isRegularFile(path) && result == CONTINUE) {
            countFile++;
        }
        return result;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {

        FileVisitResult result = visitor.postVisitDirectory(dir, e);
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

    public List<String> getIgnoredPaths() {
        return ignoredPaths;
    }
}
