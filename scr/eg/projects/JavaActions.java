package eg.projects;

import java.io.File;

import java.awt.EventQueue;
import java.awt.event.ActionListener;

import java.lang.reflect.InvocationTargetException;

//--Eadgyth--//
import eg.utils.JOptions;
import eg.console.*;
import eg.javatools.*;

import eg.ui.ViewSettings;
import eg.ui.filetree.FileTree;

/**
 * Represents a programming project in Java
 */
public class JavaActions extends ProjectConfig implements ProjectActions {

   private final ViewSettings viewSet;
   private final Compile comp;
   private final CreateJar jar;
   private final ProcessStarter proc;
   private final ConsolePanel cw;
   private final FileTree fileTree;

   private String startCommand = "";

   public JavaActions(ViewSettings viewSet, ProcessStarter proc, ConsolePanel cw,
         FileTree fileTree) {

      super(new SettingsWin(
               "Name of main class",
               "Package containing the main class",
               true,
               true, 
               "jar file"
            ),
            ".java"
      );
      this.viewSet = viewSet;
      this.proc = proc;
      this.cw = cw;
      this.fileTree = fileTree;
 
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
   
   /**
    * If a project can be successfully configured based on entries in
    * the window of this {@code SettingsWin}.
    * <p>
    * In the case of success the start command to run the Java project
    * is build.
    * @param dir  the directory of a file that maybe part of the project
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
    * If a project configuration stored in 'config' or 'prefs' can be
    * retrieved
    * <p>
    * In the case of success the start command to run the Java project
    * is build.
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
    * Passes the project's root to this {@code ProcessStarter}
    * and this {@code FileTree} and also passes the name of the
    * the directory that contain executables to this {@code FileTree}
    */
   @Override
   public void applyProject() {
      proc.addWorkingDir(getProjectPath());
      fileTree.setProjectTree(getProjectPath());
      fileTree.setDeletableDir(getExecDirName());
   }
   
   @Override
   public boolean isProjectInPath(String path) {
      return super.isProjectInPath(path);
   }
   
   @Override
   public String getProjectName() {
      return super.getProjectName();
   }
   
   @Override
   public String getExecDirName() {
      return super.getExecDirName();
   }
   
   @Override                                                                          
   public void compile() {      
      cw.setText("<<Compile " + getProjectName() + ">>\n");
      EventQueue.invokeLater(() -> {
         if (proc.isProcessEnded()) {    
            comp.compile(getProjectPath(), getExecDirName(),
                  getSourceDirName());            
            cw.setCaret(0);
            fileTree.updateTree();
            if (!viewSet.isConsoleSelected()) {
               if (!comp.success()) {
                  int result = JOptions.confirmYesNo(
                         "Compilation of '"
                        + getProjectName() + "' failed.\n"
                        + comp.getMessage() + "."
                        + "\nOpen console window to view messages?");
                  if (result == 0) {
                     viewSet.setShowConsoleState(true);
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

   /**
    * Creates a jar file and updates the project explorer
    */
   @Override
   public void build() {
      if (!mainClassFileExists()) {
         return;
      }
      String execDir = getProjectPath() + File.separator + getExecDirName();
      SearchFiles sf = new SearchFiles();
      boolean existed
         = sf.filteredFilesToArr(execDir, ".jar").length == 1;
      jar.createJar(getProjectPath(), getMainFile(),
            getModuleName(), getExecDirName(), getBuildName());
      if (!existed) {
         boolean exists = false;
         while (!exists) {
            try {
               Thread.sleep(200);
            }
            catch (InterruptedException e) {
            }
            exists
               = sf.filteredFilesToArr(execDir, ".jar").length == 1;
         }      
         fileTree.updateTree();
      }
      EventQueue.invokeLater(() -> {
         JOptions.infoMessage("Saved jar file named " + jar.getUsedJarName());
      });
   }

   private boolean mainClassFileExists() {
      boolean exists = mainProgramFileExists(".class");
      if (!exists) {
         JOptions.warnMessage("A compiled main class file could not be found");
      }
      return exists;
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