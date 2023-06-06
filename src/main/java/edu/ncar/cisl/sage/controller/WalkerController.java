package edu.ncar.cisl.sage.controller;

import edu.ncar.cisl.sage.filewalker.FileWalker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class WalkerController {
   private final FileWalker fileWalker;

    @Autowired
    public WalkerController(FileWalker fileWalker){
        this.fileWalker = fileWalker;
    }

    @GetMapping("/FileWalker")
    public String executeWalker(Model model) throws IOException {

        this.fileWalker.walkFiles(); //Will be moved out of controller and instead have a mediator between the two

        model.addAttribute("lastAccess", fileWalker.getLastAccess());
        model.addAttribute("totalFile",fileWalker.getFileCount());
        model.addAttribute("totalDirectory",fileWalker.getDirectoryCount());
        model.addAttribute("totalError",fileWalker.getErrorCount());
        model.addAttribute("runTime",fileWalker.getDuration());

        return "walker";
    }

    @GetMapping("/FileWalkerModelAndView")
    public ModelAndView executeWalker() throws IOException {

        this.fileWalker.walkFiles();

        ModelAndView modelAndView = new ModelAndView("walker");
        modelAndView.addObject("lastAccess", fileWalker.getLastAccess());
        modelAndView.addObject("totalFile",fileWalker.getFileCount());
        modelAndView.addObject("totalDirectory",fileWalker.getDirectoryCount());
        modelAndView.addObject("totalError",fileWalker.getErrorCount());
        modelAndView.addObject("runTime",fileWalker.getDuration());

        return modelAndView;
    }

}