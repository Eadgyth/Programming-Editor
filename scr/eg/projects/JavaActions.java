package eg.projects;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.EventQueue;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

//--Eadgyth--//
import eg.utils.ShowJOption;
import eg.console.*;
import eg.javatools.*;
import eg.ui.MainWin;

/**
 * Represents a programming project in Java
 */
public class JavaActions implements ProjectActions {

   private ProjectConfig projConf;

   private Compile comp;
   private CreateJar jar;
   private ProcessStarter proc;
   private ConsolePanel cw;
   private MainWin mw;
   
   private Runnable runSetCaret;
   private String startCommand = "";

   public JavaActions(MainWin mw, ProcessStarter proc, ConsolePanel cw) {
      this.mw = mw;
      this.proc = proc;
      this.cw = cw;
 
      comp = new Compile(cw);
      jar = new CreateJar(cw);
   }
   
   @Override
   public void setProjectConfig(ProjectConfig projConf) {
      this.projConf = projConf;
   }
   
   @Override
   public SettingsWin getSetWin() {
      return projConf.getSetWin();
   }
   
   @Override
   public void makeSetWinVisible(boolean isVisible) {
      projConf.makeSetWinVisible(isVisible);
   }
   
   @Override
   public void configFromSetWin(String dir, String suffix) {
      projConf.configFromSetWin(dir, suffix);
      if (projConf.getProjectRoot().length() > 0) {
         setStartCommand();
         proc.addWorkingDir(getProjectRoot());
      }
   }
   
   @Override
   public void findPreviousProjectRoot(String dir) {
      projConf.findPreviousProjectRoot(dir);
      if (projConf.getProjectRoot().length() > 0) {
         setStartCommand();
         proc.addWorkingDir(getProjectRoot());
      }
   }
   
   @Override
   public boolean isInProjectPath(String dir) {
      return projConf.isInProjectPath(dir);
   }
   
   @Override
   public String getProjectRoot() {
      return projConf.getProjectRoot();
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
            comp.compile(projConf.getProjectRoot(), projConf.getExecutableDir(),
                  projConf.getSourceDir());           
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
         jar.createJar(projConf.getProjectRoot(), projConf.getMainFile(),
               projConf.getPackageDir(), projConf.getExecutableDir(), projConf.getBuildName());
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
      if (projConf.getMainFile().length() == 0) {
         makeSetWinVisible(true);
         set = false;
      }
      return set;
   }
   
   private boolean mainClassFileExists() {
      boolean exists = projConf.mainProgramFileExists(".class");
      if (!exists) {
         ShowJOption.warnMessage("Main class file could not be found");
         exists = false;
      }
      return exists;
   }
   
   private void setStartCommand() {
      String main = projConf.getMainFile();
      if (projConf.getArgs().length() > 0) {
         main += " " + projConf.getArgs();
      }

      if (projConf.getExecutableDir().length() == 0 && projConf.getPackageDir().length() == 0 ) {
         startCommand = "java " + main;
      }
      else if (projConf.getExecutableDir().length() == 0 && projConf.getPackageDir().length() > 0 ) {
         startCommand = "java " + projConf.getPackageDir() + "." + main;
      }
      else if (projConf.getExecutableDir().length() > 0 && projConf.getPackageDir().length() == 0 ) {
         startCommand = "java -cp " + projConf.getExecutableDir() + " " + main;
      }
      else if (projConf.getExecutableDir().length() > 0 && projConf.getPackageDir().length() > 0 ) {
         startCommand = "java -cp " + projConf.getExecutableDir() + " " + projConf.getPackageDir()
               + "." + main;
      }
   }
}