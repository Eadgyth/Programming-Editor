package eg.projects;

import java.io.File;

//--Eadgyth--//
import eg.ui.ConsoleOpenable;
import eg.console.*;

/**
 * Represents a programming project in Perl
 */
public final class PerlProject extends AbstractProject implements ProjectActions {

   private final ConsoleOpenable co;
   private final ProcessStarter proc;

   private String startCmd = "";

   PerlProject(ConsoleOpenable co, ProcessStarter proc) {
      super("pl", true);
      this.co = co;
      this.proc = proc;
   }
   
   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileOption("Name of Perl script")
            .addSourceDirOption()
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
      String main = getMainFileName() + ".pl";
      if (getArgs().length() > 0) {
         main += " " + getArgs();
      }
      startCmd = "perl ";
      if (getSourceDirName().length() == 0 ) {
         startCmd += main;
      }
      else {
         String pathToMain = getSourceDirName() + "/" + main;
         startCmd += pathToMain;
      }
   }
}
