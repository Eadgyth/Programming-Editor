package eg.projects;

import java.io.File;

//--Eadgyth--//
import eg.DisplaySetter;
import eg.console.*;

/**
 * Represents a programming project in Perl
 */
public final class PerlActions extends ProjectConfig implements ProjectActions {
   
   private final DisplaySetter displSet;
   private final ProcessStarter proc;
   
   private String startCommand = "";
   
   PerlActions(DisplaySetter displSet, ProcessStarter proc) {
      super(".pl");
      this.displSet = displSet;
      this.proc = proc;
   }
   
   /**
    * Creates an adapted {@link SettingsWin}.
    */
   @Override
   public void createSettingsWin() {
      SettingsWin setWin = SettingsWin.adaptableWindow("Name of perl script", "Perl");
      setWin.addSourceDirOption()
            .addArgsOption()
            .setupWindow();
      setSettingsWin(setWin);
   }
   
   @Override
   public boolean configureProject(String dir) {
      boolean success = super.configureProject(dir);
      if (success) {
         setStartCommand();
      }
      return success;
   }

   @Override
   public boolean retrieveProject(String dir) {
      boolean success = super.retrieveProject(dir);
      if (success) {
         setStartCommand();
      }
      return success;
   }
   
   /**
    * Runs the script in the console panel
    */
   @Override
   public void runProject() {
      if (!displSet.isConsoleSelected()) {
         displSet.setShowConsoleState(true);
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
