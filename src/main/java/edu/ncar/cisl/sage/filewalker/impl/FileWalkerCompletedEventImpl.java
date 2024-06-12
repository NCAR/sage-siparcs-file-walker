package edu.ncar.cisl.sage.filewalker.impl;

import edu.ncar.cisl.sage.filewalker.FileWalkerCompletedEvent;
import org.springframework.context.ApplicationEvent;

public class FileWalkerCompletedEventImpl extends ApplicationEvent implements FileWalkerCompletedEvent {

    String id;

    public FileWalkerCompletedEventImpl(Object source) {
        super(source);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}