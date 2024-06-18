package edu.ncar.cisl.sage.filewalker;

import org.springframework.context.ApplicationEvent;

import java.nio.file.Path;

public class DirectoryCompletedEvent extends ApplicationEvent {

    String id;
    Path dir;

    public DirectoryCompletedEvent(Object source) {
        super(source);
    }

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public Path getDir() {
        return dir;
    }

    public void setDir(Path dir) { this.dir = dir; }
}
