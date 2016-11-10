package eg.projects;

import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.JButton;

import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.ShowJOption;

/**
 * The configuration of a project. A project may be retrieved from
 * the preferences by passing in the directory of a file or be newly 
 * set by entries in this {@link SettingsWin}.
 */
public class ProjectConfig {

   private static final String USER_HOME = System.getProperty("user.home");
   private static final String F_SEP = File.separator;
   
   private final Preferences prefs = new Preferences();
   
   private SettingsWin setWin; // = new ProjectSetWin();
   
   private String path = "";

   private String projectPath = "";
   private String mainFile = "";
   private String moduleDir = "";
   private String execDir = "";
   private String sourceDir = "";
   private String args = "";
   private String buildName = "";
   
   /**
    * Contructor defines which features are displayed in the project's
    * settings window
    * @param mainKind  the name of the kind of main program file
    * @param moduleKind  the name of the kind of module(e.g. package). Null
    * to skip asking for a name of a module
    * @param useArgs  true to ask for arguments in a start skript
    * @param buildKind  the name for the kind of build. Null to skip asking
    * for a build
    * May be the empty String or null
    */
   public ProjectConfig(String mainKind, String moduleKind, boolean useArgs,
         String buildKind) {
      setWin = new SettingsWin(mainKind, moduleKind, useArgs, buildKind);
      prefs.readPrefs();
   }       

   /**
    * @return  this {@link SettingsWin} object
    */
   public SettingsWin getSetWin() {
      return setWin;
   }

   public void findPreviousProjectRoot(String path) {
      this.path = path;
      findPreviousProject();
   }
   
   public boolean isInProjectPath(String path) {
      this.path = path;
      return previousProjectPath() != null;
   }  
   
   public void makeSetWinVisible(boolean isVisible) {
      setWin.makeVisible(isVisible);
   }
   
   public String getPath() {
      return path;
   }
   
   public String getProjectPath() {
      return projectPath;
   }
   
   public String getMainMethod() {
      return mainFile;
   }
   
   public String getPackageDir() {
      return moduleDir;
   }
   
   public String getExecutableDir() {
      return execDir;
   }
   
   public String getSourceDir() {
      return sourceDir;
   }
   
   /**
    * @return  the arguments for a start command as entered in the
    * text field of the ProjectSetWin window
    */
   public String getBuildName() {
      return buildName;
   }
   
   /**
    * @return  the arguments for a start command as entered in the
    * text field of the ProjectSetWin window
    */ 
   public String getArgs() {
      return args;
   }
   
   /**
    * Stores inputs in text fieds of ProjectSetWIn window and determines
    * the project root directory
    */
   public void configFromSetWin(String path, String suffix) {     
      findProjectRoot(path, suffix);
      if (projectPath.length() > 0) {
         setWin.makeVisible(false);
      }
   }
   
   /**
    * Returns true if the main program file exists in the path specified
    * by executables directory and package
    */
   public boolean mainProgramFileExists(String suffix) { 
      File target = new File(projectPath + F_SEP + execDir + F_SEP + moduleDir
            + F_SEP + mainFile + suffix);
      return target.exists();
   }

   private void findPreviousProject() {
      String previousProjectPath = previousProjectPath();
         
      if (previousProjectPath != null) {

         projectPath = previousProjectPath;
         
         mainFile = prefs.prop.getProperty("recentMain");
         setWin.displayMainFile(mainFile);
         
         moduleDir = prefs.prop.getProperty("recentModule");
         setWin.displayModule(moduleDir);
         
         sourceDir = prefs.prop.getProperty("recentSourceDir");
         setWin.displaySourcesDir(sourceDir);
         
         execDir = prefs.prop.getProperty("recentExecDir");
         setWin.displayExecDir(execDir);

         setWin.resetArgsTf();
      }
      else {
         projectPath = "";
         setWin.displayModule("");
         setWin.displayMainFile("");
         setWin.displaySourcesDir("");
         setWin.displayExecDir("");
         mainFile = "";
         setWin.resetArgsTf();
      }
   }

   private String previousProjectPath() { 
      File newFile = new File(path);
      File project = new File(prefs.prop.getProperty("recentProject"));
      String newFileStr = newFile.getPath();
      String projStr = project.getPath();

      boolean isEqual = projStr.equals(newFileStr);
     
      while(!isEqual) {
         if (newFile.getParentFile() == null) {
            newFileStr = null;
            break;
         }       
         newFile    = new File(newFile.getParent());
         newFileStr = newFile.getAbsolutePath();
         isEqual    = projStr.equals(newFileStr);
      }  
      return newFileStr;
   }
   
   /*
    * Searches project path based on entries in java settings window and the
    * path of an opened file. The project path is assigned to this 'projectPath'
    * and saved in Preferences file
    */
   private void findProjectRoot(String path, String suffix) {
      this.path = path;
      projectPath = "";
      textFieldsIn();
      File search = new File(path);
   
      String pathToSearch
            = path + F_SEP + sourceDir + F_SEP + moduleDir + F_SEP
            + mainFile + suffix;
      
      File searchPath = new File(pathToSearch);
      boolean isUserHome = false;        
      while(!searchPath.exists() & !isUserHome) {
         String newPath = search.getParent();
         search = new File(newPath);
         isUserHome = search.getPath().equals(USER_HOME);
         pathToSearch = newPath + F_SEP + sourceDir + F_SEP + moduleDir
               + F_SEP + mainFile + suffix;
         searchPath = new File(pathToSearch);
      }

      if (isUserHome) {
         ShowJOption.warnMessageToFront("A valid file could not be found");
      }
      else {
         projectPath = search.toString();
         prefs.storePrefs("recentProject", projectPath);
      }
   }
   
   private void textFieldsIn() {
      mainFile = setWin.mainFileIn();
      prefs.storePrefs("recentMain", mainFile);

      moduleDir = setWin.moduleIn();
      prefs.storePrefs("recentModule", moduleDir );
      
      sourceDir = setWin.sourcesDirIn();
      prefs.storePrefs("recentSourceDir", sourceDir);
      
      execDir = setWin.execDirIn();
      prefs.storePrefs("recentExecDir", execDir );
      
      args = setWin.argsIn();
      
      buildName = setWin.buildNameIn();
   }
}