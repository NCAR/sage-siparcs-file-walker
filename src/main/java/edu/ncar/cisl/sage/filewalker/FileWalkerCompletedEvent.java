package edu.ncar.cisl.sage.filewalker;

import org.springframework.context.ApplicationEvent;

public class FileWalkerCompletedEvent extends ApplicationEvent {

    String id;

    public FileWalkerCompletedEvent(Object source) {
        super(source);
    }

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}