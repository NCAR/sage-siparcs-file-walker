package edu.ncar.cisl.sage.mediator;

import edu.ncar.cisl.sage.filewalker.impl.DirectoryCompletedEventImpl;
import edu.ncar.cisl.sage.filewalker.impl.FileWalkerCompletedEventImpl;
import edu.ncar.cisl.sage.repository.EsDirStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DirectoryStateMediator {

    private final EsDirStateRepository repository;

    @Autowired
    public DirectoryStateMediator(EsDirStateRepository repository) {

        this.repository = repository;
    }

    @EventListener
    public void handleDirCompletedEvent(DirectoryCompletedEventImpl event) throws IOException {

        this.repository.directoryCompleted(event.getId(),event.getDir(),event.getStartingPath());
    }

    @EventListener
    public void handleFileWalkerCompletedEvent(FileWalkerCompletedEventImpl event) throws IOException {

        this.repository.deleteDirectoryState(event.getId());
    }
}
