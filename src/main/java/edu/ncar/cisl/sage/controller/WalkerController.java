package edu.ncar.cisl.sage.controller;

import edu.ncar.cisl.sage.filewalker.FileWalker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
public class WalkerController implements Runnable{
   private final FileWalker fileWalker;

    @Autowired
    public WalkerController(FileWalker fileWalker) {
        this.fileWalker = fileWalker;
    }

    @RequestMapping(value="/FileWalker", method={RequestMethod.GET})
    public String executeWalker(Model model) throws IOException {

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

   @Override
   public void run() {
       try {
           this.fileWalker.walkFiles();
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
//       System.out.println("In Run");
   }

//   if(!this.fileWalker.isRunning()){
//       Runnable runnable = new Runnable() {
//           @Override
//           public void run() {
//               try {
//                   executeWalker();
//               } catch (IOException e) {
//                   throw new RuntimeException(e);
//               }
//           }
//       };
//        Thread thread = new Thread(runnable);
//       thread.start();
//    }
    @RequestMapping(value="/FileWalker", method={RequestMethod.POST})
    public String executeWalker() throws IOException{

        if(!this.fileWalker.isRunning()){
            Runnable runnable = new WalkerController(this.fileWalker);
            Thread t = new Thread(runnable);
            t.start();
        }
//        System.out.println("In Execution");
//        this.fileWalker.walkFiles();

        return "redirect:/FileWalker";
    }
}

//    Nathan's Version
//    @GetMapping("/FileWalkerModelAndView")
//    public ModelAndView executeWalker() throws IOException {
//
//        this.fileWalker.walkFiles();
//
//        ModelAndView modelAndView = new ModelAndView("walker");
//        modelAndView.addObject("lastAccess", fileWalker.getLastAccess());
//        modelAndView.addObject("totalFile",fileWalker.getFileCount());
//        modelAndView.addObject("totalDirectory",fileWalker.getDirectoryCount());
//        modelAndView.addObject("totalError",fileWalker.getErrorCount());
//        modelAndView.addObject("runTime",fileWalker.getDuration());
//
//        return modelAndView;
//    }