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
    private long countError = 0;

    private long countLink = 0;

    private long countExtra = 0;

    private final List<String> ignoredPaths;

    public LoggingFileVisitor(List<String> ignoredPaths) {
        this.ignoredPaths = ignoredPaths;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException{

        // TODO change to stream.
        // TODO ignore symbolic links?
        for(int i = 0; i < this.ignoredPaths.size(); i++){
            if(dir.toString().contains(this.ignoredPaths.get(i))) {
                System.out.println("Skipping subtree" + ignoredPaths.get(i));
                return SKIP_SUBTREE;
            }
        }
        return CONTINUE;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException{

        System.out.println(path);

        if (Files.isDirectory(path)) {
            countDirectory++;
        }
        else if (Files.isRegularFile(path)) {
            countFile++;
        }
        else if (Files.isSymbolicLink(path)) {
            countLink++;
        }
        else {
            countExtra++;
        }

        return CONTINUE;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException{
        return CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException{
        System.out.println("Failed to access file: " + file);
        countError++;
        return CONTINUE;
    }

    public void reset() {
        countError = 0;
        countFile = 0;
        countDirectory = 0;
        countLink = 0;
        countExtra = 0;
    }

    public long getCountFile(){
        return countFile;
    }

    public long getCountDirectory(){
        return countDirectory;
    }

    public long getCountError(){
        return countError;
    }

    public long getCountLink(){
        return countLink;
    }

    public long getCountExtra() {
        return countExtra;
    }
}
