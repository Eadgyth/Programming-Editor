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
   public void createSettingsWin() {
      setWin = SettingsWin.adaptableWindow();
      setWin.addFileOption("Name of Perl script")
            .addSourceDirOption()
            .addArgsOption()
            .setupWindow();
   }

   /**
    * {@inheritDoc}
    * <p>Creates the start command to run the Perl script
    */
   @Override
   public boolean configureProject(String dir) {
      boolean success = super.configureProject(dir);
      if (success) {
         setStartCommand();
      }
      return success;
   }

   /**
    * {@inheritDoc}
    * Creates the start command to run the Perl script
    */
   @Override
   public boolean retrieveProject(String dir) {
      boolean success = super.retrieveProject(dir);
      if (success) {
         setStartCommand();
      }
      return success;
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

   //
   //--private--/
   //

   private void setStartCommand() {
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
