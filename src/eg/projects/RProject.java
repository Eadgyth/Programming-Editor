package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--//
import eg.console.*;
import eg.utils.FileUtils;
import eg.ui.ConsoleOpenable;

/**
 * Represents a programming project in R
 */
public final class RProject extends AbstractProject implements ProjectActions {
   
   private final ConsoleOpenable co;
   private final ProcessStarter proc;

   private String startCmd = "";

   RProject(ConsoleOpenable co, ProcessStarter proc) {
      super("R", true);
      this.co = co;
      this.proc = proc;
   }
   
   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileOption("Name of R script")
         .addStartOptOption()
         .addArgsOption()
         .buildWindow();
   }

   @Override
   public void runProject() {
      if (!co.isConsoleOpen()) {
         co.openConsole();
      }
      proc.startProcess(startCmd);
   }
   
   @Override
   protected void setCommandParameters() {
      String main = getMainFileName() + ".R";
      if (getArgs().length() > 0) {
         main += " " + getArgs();
      }
      startCmd = "Rscript ";
      if (getStartOptions().length() > 0) {
         startCmd += getStartOptions() + " ";
      }
      startCmd += main;
   }
}
