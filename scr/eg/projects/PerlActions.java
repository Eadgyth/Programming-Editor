package eg.projects;

import java.io.File;

//--Eadgyth--//
import eg.DisplaySetter;
import eg.console.*;
import eg.ui.filetree.FileTree;

/**
 * Represents a programming project in Perl
 */
public final class PerlActions extends ProjectConfig implements ProjectActions {
   
   private final DisplaySetter displSet;
   private final ProcessStarter proc;
   private final ConsolePanel cw;
   private final FileTree fileTree;
   
   private String startCommand = "";
   
   PerlActions(DisplaySetter displSet, ProcessStarter proc,
         ConsolePanel cw, FileTree fileTree) {

      super(".pl");
      this.displSet = displSet;
      this.proc = proc;
      this.cw = cw;
      this.fileTree = fileTree;
   }
   
   /**
    * Creates an adapted {@link SettingsWin}.
    */
   @Override
   public void createSettingsWin() {
      SettingsWin setWin = SettingsWin.adaptableWindow();
      setWin.useNewFileLabel("Name of perl script")
            .useSourceDir()
            .useArgs()
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
   
   /**
    * If a project configuration stored in 'config' or 'prefs' can be
    * retrieved
    * @param dir  the directory of a file that maybe part of the project
    * @return  If a project configuration stored in 'config' or 'prefs'
    * can be retrieved
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
    * Not used
    */
   @Override
   public void compile() {
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
   
   /**
    * Not used
    */
   @Override
   public void build() {
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
