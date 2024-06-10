package edu.ncar.cisl.sage.filewalker.impl;

import edu.ncar.cisl.sage.filewalker.DirectoryCompletedEvent;
import org.springframework.context.ApplicationEvent;

import java.nio.file.Path;

public class DirectoryCompletedEventImpl extends ApplicationEvent implements DirectoryCompletedEvent {

    String id;
    Path dir;
    Path startingPath;

    public DirectoryCompletedEventImpl(Object source) {
        super(source);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    @Override
    public Path getDir() {
        return dir;
    }

    public void setDir(Path dir) { this.dir = dir; }

    @Override
    public Path getStartingPath() {
        return startingPath;
    }

    public void setStartingPath(Path startingPath) { this.startingPath = startingPath; }
}
