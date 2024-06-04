package edu.ncar.cisl.sage.filewalker;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class DirStateFileVisitor implements FileVisitor<Path> {

    private Set<Path> completed;
    private Set<Path> inProgress;

    public DirStateFileVisitor(Set<Path> completed, Set<Path> inProgress) {

        this.completed = completed;
        this.inProgress = inProgress;

        //Test
        this.completed.add(Path.of("/Users/phuongan/.config"));
        this.completed.add(Path.of("/Users/phuongan/Music"));
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

        // skip subtree if already visited
        boolean visited = completed.stream()
                .filter(path -> dir.toString().startsWith(path.toString()))
                .anyMatch(m -> true);
        FileVisitResult result = CONTINUE;
        if(visited){
            System.out.println(dir + "    skip");
            result = SKIP_SUBTREE;
        } else {
            System.out.println(dir + "    pre");
            inProgress.add(dir);
        }
        return result;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) {

        return CONTINUE;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException e) {

        inProgress.remove(dir);
        completed.add(dir);

        // remove keys that contain already visited path
        completed.removeIf(path -> path.startsWith(dir) && !dir.equals(path));

        System.out.println(dir + "   post");
        System.out.println("Size of completed:  " + completed.size());
        return CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path path, IOException e) {

        return CONTINUE;
    };

}
