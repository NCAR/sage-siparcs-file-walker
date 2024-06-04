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

public class CompositeFileVisitor implements FileVisitor<Path> {

    private final List<FileVisitor<Path>> visitors;

    public CompositeFileVisitor(List<FileVisitor<Path>> visitors) {

        this.visitors = visitors;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

        FileVisitResult result = CONTINUE;
        boolean check = visitors.stream()
                .map(v -> {
                    try {
                        return v.preVisitDirectory(dir, attrs);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .allMatch(m -> m == CONTINUE);
        if(!check){
            result = SKIP_SUBTREE;
        }

        return result;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) {

        visitors.forEach(v -> {
            try {
                v.visitFile(path, attr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return CONTINUE;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException e) {

        visitors.forEach(v -> {
            try {
                v.postVisitDirectory(dir, e);
            } catch (IOException exp) {
                throw new RuntimeException(exp);
            }
        });

        return CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path path, IOException e) {

        visitors.forEach(v -> {
            try {
                v.visitFileFailed(path, e);
            } catch (IOException exp) {
                throw new RuntimeException(exp);
            }
        });

        return CONTINUE;
    }
}