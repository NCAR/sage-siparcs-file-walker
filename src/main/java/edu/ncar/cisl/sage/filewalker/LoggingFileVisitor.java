package edu.ncar.cisl.sage.filewalker;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import java.util.*;

public class LoggingFileVisitor implements FileVisitor<Path> {

    private long countFile = 0;

    private long countDirectory = 0;
    private long countErrorOther = 0;

    private long countErrorDirectory = 0;

    private final List<String> ignoredPaths;

    public LoggingFileVisitor(List<String> ignoredPaths) {
        this.ignoredPaths = ignoredPaths;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

        // TODO change to stream.
        if(Files.isSymbolicLink(dir)){
            //System.out.println("Skipping Symbolic Link: " + dir);
            return SKIP_SUBTREE;
        }
        for(int i = 0; i < this.ignoredPaths.size(); i++){
            if(dir.toString().contains(this.ignoredPaths.get(i))) {
                //System.out.println("Skipping subtree: " + ignoredPaths.get(i));
                return SKIP_SUBTREE;
            }
        }
        if (Files.isDirectory(dir)){
            countDirectory++;
        }
        return CONTINUE;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) {

        if (Files.isRegularFile(path)) {
            countFile++;
            //System.out.println(path);
        }
        return CONTINUE;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException e) {
        return CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path path, IOException e) {

        if (Files.isDirectory(path)) {
            countErrorDirectory++;
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        else {
            countErrorOther++;
            System.out.println("Failed to access: " + e.getMessage());
        }

        return CONTINUE;
    }

    public void reset() {
        countErrorOther = 0;
        countErrorDirectory = 0;
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

    public long getCountErrorDirectory() {
        return countErrorDirectory;
    }

    public List<String> getIgnoredPaths() {
        return ignoredPaths;
    }
}
