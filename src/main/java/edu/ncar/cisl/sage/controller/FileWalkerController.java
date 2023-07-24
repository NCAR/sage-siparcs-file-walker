package edu.ncar.cisl.sage.controller;

import edu.ncar.cisl.sage.filewalker.FileWalker;
import edu.ncar.cisl.sage.repository.FileWalkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class FileWalkerController {

    private final FileWalkerRepository repository;

    @Autowired
    public FileWalkerController(FileWalkerRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value="/file-walker-list", method={RequestMethod.GET})
    public String viewWalkers(Model model) {

        model.addAttribute("fileWalkers", this.repository.getAll());

        return "file-walkers";
    }

    @RequestMapping(value="/file-walker-list", method={RequestMethod.POST})
    public String executeWalkers(@RequestParam(name = "confirm") boolean confirm) {

        if (confirm) {

            this.repository.getAll().stream()
                    .forEach(fw -> startFileWalkerThread(fw));
        }

        return "redirect:/file-walker-list";
    }

    private void startFileWalkerThread(FileWalker fileWalker) {

        if (!fileWalker.isRunning()) {

            Runnable runnable = () -> {
                try {

                    fileWalker.walkFiles();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    @RequestMapping(value="/file-walker-list-confirm", method={RequestMethod.POST})
    public String confirmFileWalkers(){

        return "file-walkers-confirm";
    }
}
