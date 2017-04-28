package eg.projects;

import java.io.File;

//--Eadgyth--//
import eg.ProjectUIUpdate;
import eg.console.*;

/**
 * Represents a programming project in Perl
 */
public final class PerlActions extends ProjectConfig implements ProjectActions {
   
   private final ProjectUIUpdate update;
   private final ProcessStarter proc;
   
   private String startCommand = "";
   
   PerlActions(ProjectUIUpdate update, ProcessStarter proc) {
      super(".pl");
      this.update = update;
      this.proc = proc;
   }
   
   /**
    * Creates an adapted {@link SettingsWin}.
    */
   @Override
   public void createSettingsWin() {
      SettingsWin setWin = SettingsWin.adaptableWindow("Name of Perl script");
      setWin.addSourceDirOption()
            .addArgsOption()
            .setupWindow();
      setSettingsWin(setWin);
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
      if (!update.isConsoleOpen()) {
         update.openConsole();
      }
      proc.startProcess(startCommand);
   }
   
   private void setStartCommand() {
      String main = getMainFile() + ".pl";
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
