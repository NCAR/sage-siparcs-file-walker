package edu.ncar.cisl.sage.mediator;

import edu.ncar.cisl.sage.filewalker.DirectoryCompletedEvent;
import edu.ncar.cisl.sage.filewalker.FileWalkerCompletedEvent;
import edu.ncar.cisl.sage.repository.EsDirectoryStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DirectoryStateMediator {

    private final EsDirectoryStateRepository repository;

    @Autowired
    public DirectoryStateMediator(EsDirectoryStateRepository repository) {

        this.repository = repository;
    }

    @EventListener
    public void handleDirCompletedEvent(DirectoryCompletedEvent event) {

        this.repository.directoryCompleted(event.getId(),event.getDir());
    }

    @EventListener
    public void handleFileWalkerCompletedEvent(FileWalkerCompletedEvent event) {

        this.repository.removeDirectoryState(event.getId());
    }
}
