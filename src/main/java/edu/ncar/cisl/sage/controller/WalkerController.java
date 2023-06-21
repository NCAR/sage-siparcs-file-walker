package edu.ncar.cisl.sage.controller;

import edu.ncar.cisl.sage.filewalker.FileWalker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
public class WalkerController {
   private final FileWalker fileWalker;

    @Autowired
    public WalkerController(FileWalker fileWalker) {
        this.fileWalker = fileWalker;
    }

    @RequestMapping(value="/FileWalker", method={RequestMethod.GET})
    public String executeWalker(Model model) {

        model.addAttribute("startingPath", this.fileWalker.getStartingPath());
        model.addAttribute("lastStarted", this.fileWalker.getLastAccess());
        model.addAttribute("totalFile", this.fileWalker.getFileCount());
        model.addAttribute("totalDirectory", this.fileWalker.getDirectoryCount());
        model.addAttribute("totalError", this.fileWalker.getErrorCount());
        model.addAttribute("totalLink", this.fileWalker.getLinkCount());
        model.addAttribute("totalExtra", this.fileWalker.getExtraCount());
        model.addAttribute("runTime", this.fileWalker.getDuration());
        model.addAttribute("state", this.fileWalker.isRunning());

        return "walker";
    }

    @RequestMapping(value="/FileWalker", method={RequestMethod.POST})
    public String executeWalker() {

        if (!this.fileWalker.isRunning()) {

            Runnable runnable = () -> {
                try {

                    WalkerController.this.fileWalker.walkFiles();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }

        return "redirect:/FileWalker";
    }
}