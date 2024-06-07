package edu.ncar.cisl.sage.mediator;

import edu.ncar.cisl.sage.filewalker.impl.DirectoryCompletedEventImpl;
import edu.ncar.cisl.sage.repository.EsDirStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DirStateMediator {

    private final EsDirStateRepository repository;

    @Autowired
    public DirStateMediator(EsDirStateRepository repository) {

        this.repository = repository;
    }

    @EventListener
    public void handleDirCompletedEvent(DirectoryCompletedEventImpl event) throws IOException {

        this.repository.directoryCompleted(event.getId(),event.getDir());
    }
}
