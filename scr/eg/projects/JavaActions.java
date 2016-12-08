package eg.projects;

import java.awt.EventQueue;
import java.awt.event.ActionListener;

import java.lang.reflect.InvocationTargetException;

//--Eadgyth--//
import eg.utils.JOptions;
import eg.console.*;
import eg.javatools.*;
import eg.ui.ViewSettings;

/**
 * Represents a programming project in Java
 */
public class JavaActions extends ProjectConfig implements ProjectActions {

   private final ViewSettings viewSet;
   private final Compile comp;
   private final CreateJar jar;
   private final ProcessStarter proc;
   private final ConsolePanel cw;

   private String startCommand = "";

   public JavaActions(ViewSettings viewSet, ProcessStarter proc, ConsolePanel cw) {
      super(new SettingsWin("Name of main class", "Package containing the main class",
           true, true, "jar file"));
      this.viewSet = viewSet;
      this.proc = proc;
      this.cw = cw;
 
      comp = new Compile(cw);
      jar = new CreateJar(cw);
   }
   
   @Override
   public void addOkAction(ActionListener al) {
      super.addOkAction(al);
   }
   
   @Override
   public void makeSetWinVisible(boolean isVisible) {
      super.makeSetWinVisible(isVisible);
   }
   
   @Override
   public boolean configFromSetWin(String dir, String suffix) {
      boolean success = super.configFromSetWin(dir, suffix);
      if (success) {
         setStartCommand();
      }
      return success;
   }
   
   @Override
   public boolean findPreviousProjectRoot(String dir) {
      boolean success = super.findPreviousProjectRoot(dir);
      if (success) {
         setStartCommand();
      }
      return success;
   }

   /**
    * Returns the projects path and passes the path to this
    * {@code ProcessStarter}
    * @return  the project's root dirrectory
    */
   @Override
   public String applyProjectRoot() {
      proc.addWorkingDir(super.getProjectRoot());
      return super.getProjectRoot();
   }
   
   @Override
   public boolean isInProjectPath(String dir) {
      return super.isInProjectPath(dir);
   }
   
   @Override
   public String getProjectName() {
      return super.getProjectName();
   }
   
   @Override
   public String getExecutableDir() {
      return super.getExecutableDir();
   }

   @Override
   public void storeConfig() {
      super.storeConfig();
   }
   
   @Override                                                                          
   public void compile() {      
      cw.setText("");
      EventQueue.invokeLater(() -> {
         if (proc.isProcessEnded()) {    
            comp.compile(applyProjectRoot(), getExecutableDir(),
                         getSourceDir());            
         }
         cw.setCaret(0);
         if (!viewSet.isConsoleSelected()) {
            if (!comp.success()) {
               int result = JOptions.confirmYesNo("Compilation failed\n"
                     + comp.getMessage()
                     + "\nOpen console window to view messages?");
               if (result == 0) {
                  viewSet.setShowConsoleState(true);
               }
            }
            else {
               JOptions.infoMessage("Compilation successful");
            }
         }
      });
   }

   @Override
   public void runProject() {
      if (!mainClassFileExists()) {
         return;
      }
      if (!viewSet.isConsoleSelected()) {
         viewSet.setShowConsoleState(true);
      }
      proc.startProcess(startCommand);
   }

   @Override
   public void build() {
      if (!mainClassFileExists()) {
         return;
      }
      cw.setText("");
      jar.createJar(applyProjectRoot(), getMainFile(),
            getModuleDir(), getExecutableDir(), getBuildName());
   }

   private boolean mainClassFileExists() {
      boolean exists = mainProgramFileExists(".class");
      if (!exists) {
         JOptions.warnMessage("Main class file could not be found");
         exists = false;
      }
      return exists;
   }
   
   private void setStartCommand() {
      String main = getMainFile();
      if (getArgs().length() > 0) {
         main += " " + getArgs();
      }

      if (getExecutableDir().length() == 0 && getModuleDir().length() == 0 ) {
         startCommand = "java " + main;
      }
      else if (getExecutableDir().length() == 0 && getModuleDir().length() > 0 ) {
         startCommand = "java " + getModuleDir() + "." + main;
      }
      else if (getExecutableDir().length() > 0 && getModuleDir().length() == 0 ) {
         startCommand = "java -cp " + getExecutableDir() + " " + main;
      }
      else if (getExecutableDir().length() > 0 && getModuleDir().length() > 0 ) {
         startCommand = "java -cp " + getExecutableDir() + " " + getModuleDir()
               + "." + main;
      }
   }
}