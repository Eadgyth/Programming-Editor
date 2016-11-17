package eg.projects;

import java.awt.EventQueue;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

//--Eadgyth--//
import eg.utils.ShowJOption;
import eg.console.*;
import eg.javatools.*;
import eg.ui.MainWin;

/**
 * Represents a programming project in Java
 */
public class JavaActions extends ProjectConfig implements ProjectActions {

   private final Compile comp;
   private final CreateJar jar;
   private final ProcessStarter proc;
   private final ConsolePanel cw;
   private final MainWin mw;
   
   private String startCommand = "";

   public JavaActions(MainWin mw, ProcessStarter proc, ConsolePanel cw) {
      super(new SettingsWin("Name of main class", "Package containing the main class",
           true, true, "jar file"));

      this.mw = mw;
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
         proc.addWorkingDir(getProjectRoot());
      }
      return success;
   }
   
   @Override
   public boolean findPreviousProjectRoot(String dir) {
      boolean success = super.findPreviousProjectRoot(dir);
      if (success) {
         setStartCommand();
         proc.addWorkingDir(getProjectRoot());
      }
      return success;
   }
   
   @Override
   public boolean isInProjectPath(String dir) {
      return super.isInProjectPath(dir);
   }
   
   @Override
   public String getProjectRoot() {
      return super.getProjectRoot();
   }
   
   @Override                                                                          
   public void compile() {
      if (!isProjectSet()) {
         return;
      }
      if (!proc.isProcessEnded()) {
         return;
      }
      cw.setText("");
      EventQueue.invokeLater(() -> {
         try {
            mw.setCursor(MainWin.BUSY_CURSOR);
            comp.compile(getProjectRoot(), getExecutableDir(),
                  getSourceDir());           
         }
         catch(Exception e) {
            e.printStackTrace();
         }
         finally {
            mw.setCursor(MainWin.DEF_CURSOR); 
         }
         EventQueue.invokeLater(() -> cw.setCaret(0));

         if (!mw.isConsoleSelected()) {
            if (comp.isCompiled()) {
               ShowJOption.infoMessage("Compilation successful");
            }
            else {
               int result = ShowJOption.confirmYesNo("Compilation failed\n"
                     + comp.getFirstError()
                     + "\nOpen console window to view messages?");
               if (result == JOptionPane.YES_OPTION) {
                  mw.showConsole();
               }
            }
         }
      });
   }

   @Override
   public void runProject() {
      if (!isProjectSet()) {
         return;
      } 
      if (!mainClassFileExists()) {
         return;
      }
      if (!proc.isProcessEnded()) {
         return;
      }
      if (!mw.isConsoleSelected()) {
         mw.showConsole();
      }
      proc.startProcess(startCommand);
   }

   @Override
   public void build() {
      if (!isProjectSet()) {
         return;
      } 
      if (!mainClassFileExists()) {
         return;
      }
      cw.setText("");
      EventQueue.invokeLater(() -> {
         jar.createJar(getProjectRoot(), getMainFile(),
               getPackageDir(), getExecutableDir(), getBuildName());
         String info = "Saved jar file named " + jar.getUsedJarName();
         ShowJOption.infoMessage(info);
      });
   }
   
   private void askForSettings() {
      int result = ShowJOption.confirmYesNo("Set project?");
      if (result == JOptionPane.YES_OPTION) {         
         makeSetWinVisible(true);
      }
   }
   
   private boolean isProjectSet() {
      boolean set = true;
      if (getMainFile().length() == 0) {
         makeSetWinVisible(true);
         set = false;
      }
      return set;
   }
   
   private boolean mainClassFileExists() {
      boolean exists = mainProgramFileExists(".class");
      if (!exists) {
         ShowJOption.warnMessage("Main class file could not be found");
         exists = false;
      }
      return exists;
   }
   
   private void setStartCommand() {
      String main = getMainFile();
      if (getArgs().length() > 0) {
         main += " " + getArgs();
      }

      if (getExecutableDir().length() == 0 && getPackageDir().length() == 0 ) {
         startCommand = "java " + main;
      }
      else if (getExecutableDir().length() == 0 && getPackageDir().length() > 0 ) {
         startCommand = "java " + getPackageDir() + "." + main;
      }
      else if (getExecutableDir().length() > 0 && getPackageDir().length() == 0 ) {
         startCommand = "java -cp " + getExecutableDir() + " " + main;
      }
      else if (getExecutableDir().length() > 0 && getPackageDir().length() > 0 ) {
         startCommand = "java -cp " + getExecutableDir() + " " + getPackageDir()
               + "." + main;
      }
   }
}