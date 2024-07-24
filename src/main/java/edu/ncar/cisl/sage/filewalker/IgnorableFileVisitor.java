package edu.ncar.cisl.sage.filewalker;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class IgnorableFileVisitor implements FileVisitor<Path> {

    private final FileVisitor<Path> visitor;
    private final List<String> ignoredPaths;

    public IgnorableFileVisitor(FileVisitor<Path> visitor, List<String> ignoredPaths) {

        this.visitor = visitor;
        this.ignoredPaths = ignoredPaths;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        FileVisitResult result = SKIP_SUBTREE;
        if(!Files.isSymbolicLink(dir) && !this.ignoredPaths.stream()
                .filter(path -> dir.toString().contains(path))
                .anyMatch(m -> true)) {
            result = this.visitor.preVisitDirectory(dir, attrs);
        }
        return result;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        FileVisitResult result = CONTINUE;
        if(!Files.isSymbolicLink(file)) {
            result = this.visitor.visitFile(file, attrs);
        }
        return result;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {

        return visitor.visitFileFailed(file, exc);
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

        return visitor.postVisitDirectory(dir, exc);
    }
}
