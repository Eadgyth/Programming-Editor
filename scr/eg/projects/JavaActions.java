package eg.projects;

import java.io.File;
import java.io.IOException;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.DisplaySetter;
import eg.console.*;
import eg.javatools.*;
import eg.utils.JOptions;
import eg.utils.FileUtils;
import eg.ui.filetree.FileTree;

/**
 * Represents a programming project in Java.
 */
public final class JavaActions extends ProjectConfig
      implements ProjectActions {

   private final static String F_SEP = File.separator;

   private final DisplaySetter displSet;
   private final Compile comp;
   private final CreateJar jar;
   private final ProcessStarter proc;
   private final ConsolePanel consPnl;
   private final FileTree fileTree;

   private String startCommand = "";

   JavaActions(DisplaySetter displSet, ProcessStarter proc,
         ConsolePanel consPnl, FileTree fileTree) {

      super(".java");
      this.displSet = displSet;
      this.proc = proc;
      this.consPnl = consPnl;
      this.fileTree = fileTree;
 
      comp = new Compile(consPnl);
      jar = new CreateJar(consPnl);
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
    * Passes the project's root directory to this {@link ProcessStarter}
    * and this {@link FileTree} and also passes the name of the
    * the directory that contain executables to this {@code FileTree}
    */
   @Override
   public void applyProject() {
      proc.addWorkingDir(getProjectPath());
      fileTree.setProjectTree(getProjectPath());
      fileTree.setDeletableDirName(getExecDirName());
   }
   
   @Override                                                                          
   public void compile() {      
      consPnl.setText("<<Compile " + getProjectName() + ">>\n");
      EventQueue.invokeLater(() -> {
         if (proc.isProcessEnded()) {    
            comp.compile(getProjectPath(), getExecDirName(),
                  getSourceDirName());            
            consPnl.setCaret(0);
            fileTree.updateTree();
            if (!displSet.isConsoleSelected()) {
               if (!comp.success()) {
                  int result = JOptions.confirmYesNo(
                        "Compilation of '"
                        + getProjectName() + "' failed.\n"
                        + comp.getMessage() + "."
                        + "\nOpen console window to view messages?");
                  if (result == 0) {
                     displSet.setShowConsoleState(true);
                  }
               }
               else {
                  JOptions.infoMessage(
                        "Successfully compiled '"
                        + getProjectName() + "'.");
               }
            }
         }
      });
   }

   /**
    * Runs the Java program in the console panel
    */
   @Override
   public void runProject() {
      if (!mainClassFileExists()) {
         return;
      }
      if (!displSet.isConsoleSelected()) {
         displSet.setShowConsoleState(true);
      }
      proc.startProcess(startCommand);
   }

   /**
    * Creates a jar file
    */
   @Override
   public void build() {
      if (!mainClassFileExists()) {
         return;
      }
      String jarName = getBuildName();
      if (jarName.length() == 0) {
         jarName = getMainFile();
      }
      boolean existed = jarFileExists(jarName);
      try {
         consPnl.setText("");
         jar.createJar(getProjectPath(), getMainFile(),
               getModuleName(), getExecDirName(), jarName);
         if (!existed) {
            boolean exists = false;
            while (!exists) {
               try {
                  Thread.sleep(200);
               }
               catch (InterruptedException e) {
               }
               exists = jarFileExists(jarName);
            }
            fileTree.updateTree();
         }
         consPnl.appendText("<<Saved jar file named " + jarName + ">>\n");
         JOptions.infoMessage("Saved jar file named " + jarName);
      }
      catch (IOException e) {
         FileUtils.logStack(e);
      }        
   }
   
   @Override
   protected void configSettingsWin(SettingsWin setWin) {
      setWin.configWindow(
            "Name of main class",
            "Package containing the main class",
            true,
            true,
            true,
            "jar file");
   }

   private boolean mainClassFileExists() {
      boolean exists = mainProgramFileExists(".class");
      if (!exists) {
         JOptions.warnMessage("A compiled main class file could not be found");
      }
      return exists;
   }

   private boolean jarFileExists(String jarName) {
      String execDir = getProjectPath() + F_SEP + getExecDirName();
      return new File(execDir + F_SEP + jarName + ".jar").exists();
   }
   
   private void setStartCommand() {
      String main = getMainFile();
      if (getArgs().length() > 0) {
         main += " " + getArgs();
      }
      if (getExecDirName().length() == 0 && getModuleName().length() == 0 ) {
         startCommand = "java " + main;
      }
      else if (getExecDirName().length() == 0 && getModuleName().length() > 0 ) {
         startCommand = "java " + getModuleName() + "." + main;
      }
      else if (getExecDirName().length() > 0 && getModuleName().length() == 0 ) {
         startCommand = "java -cp " + getExecDirName() + " " + main;
      }
      else if (getExecDirName().length() > 0 && getModuleName().length() > 0 ) {
         startCommand = "java -cp " + getExecDirName() + " " + getModuleName()
               + "." + main;
      }
   }
}
