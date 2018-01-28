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

   private String startCommand = "";

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

   /**
    * Runs the Perl script and shows output in the console panel
    */
   @Override
   public void runProject() {
      if (!co.isConsoleOpen()) {
         co.openConsole();
      }
      proc.startProcess(startCommand);
   }
   
   @Override
   protected void setCommandParameters() {
      String main = getMainFileName() + ".pl";
      if (getArgs().length() > 0) {
         main += " " + getArgs();
      }
      if (getSourceDirName().length() == 0 ) {
         startCommand = "perl " + main;
      }
      else {
         startCommand = "perl "
               + getSourceDirName()
               + File.separator
               + main;
      }
   }
}
