package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.Preferences;

import eg.utils.JOptions;

/**
 * Represents the configuration of a project. <br>
 * Class implements methods in {@link Configurable} except
 * {@link Configurable #applyProjectPath()}
 */
public abstract class ProjectConfig implements Configurable {

   private final static String F_SEP = File.separator;
   private final static String CONFIG_FILE = "config.properties";   
   private final static Preferences PREFS = new Preferences();
   private final static Preferences CONFIG = new Preferences();

   private final SettingsWin setWin;
   private final String suffix;

   private String projectPath = "";
   private String mainFile = "";
   private String moduleDir = "";
   private String execDir = "";
   private String sourceDir = "";
   private String args = "";
   private String buildName = "";
   
   /**
    * @param setWin  the reference to an object of {@link SettingsWin}
    * which is set up to ask for the desired inputs
    */
   public ProjectConfig(SettingsWin setWin, String suffix) {
      this.setWin = setWin;
      this.suffix = suffix;
   }

   @Override
   public void addOkAction(ActionListener al) {
      setWin.okAct(al);
   }
   
   @Override
   public void makeSetWinVisible(boolean isVisible) {
      setWin.makeVisible(isVisible);
   }
   
   @Override
   public boolean configureProject(String dir) {    
      projectPath = findRootByFile(dir, pathRelToRoot());
      boolean success = storeInputs();
      if (success) {
         setWin.makeVisible(false);
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
      findSavedProject(dir);
      return projectPath.length() > 0;
   }
   
   @Override
   public boolean isInProjectPath(String dir) {
      return findRootInPath(dir, PREFS).length() > 0;
   }
   
   @Override
   public String getProjectName() {
      File f = new File(projectPath);
      return f.getName();
   }
   
   /**
    * Returns the project's root directory
    */
   protected String getProjectRoot() {
      return projectPath;
   }
   
   /**
    * Returns the the name of the project's main file
    * @return  the name of project's main file
    */ 
   protected String getMainFile() {
      return mainFile;
   }

   /**
    * Returns the name of the directory of a module.
    * <p>
    * It is not specified what a 'module' is. In the case of a Java
    * project a module would be a package
    * @return  the name of the directory of a module.
    */ 
   protected String getModuleName() {
      return moduleDir;
   }
   
   /**
    * Returns the name of the directoy where source files are
    * saved
    * @return  the name of the directoy where source files are
    * saved
    */ 
   protected String getSourceDirName() {
      return sourceDir;
   }
   
   /**
    * Returns the name of the directoy where executable files are
    * saved
    * @return  the name of the directoy where executable files are
    * saved
    */
   protected String getExecDirName() {
      return execDir;
   }

   /**
    * @return  the name for a build entered in the
    * text field of this {@code SettingsWin}
    */ 
   protected String getBuildName() {
      return buildName;
   }
   
   /**
    * @return  the arguments for a start command entered in the
    * text field of this {@code SettingsWin}
    */ 
   protected String getArgs() {
      return args;
   }
   
   /**
    * If the main executable file exists
    * <p>
    * @param suffix  the extension of the project's main file
    * @return  true if the main executable file exists
    */
   protected boolean mainProgramFileExists(String suffix) { 
      File target = new File(projectPath + F_SEP + execDir + F_SEP + moduleDir
            + F_SEP + mainFile + suffix);
      return target.exists();
   }
   
   //
   //--private--
   //

   private void findSavedProject(String dir) {
      String previousProjectRoot = "";
      Preferences props = null;
      //
      // firstly see if there is a config file
      previousProjectRoot = findRootByFile(dir, CONFIG_FILE);
      if (previousProjectRoot.length() > 0) {
         props = CONFIG;
         props.readConfig(previousProjectRoot);
         setWin.setSaveConfigSelected(true);
      }
      //
      // then see if the dir includes the project root in prefs
      else {
         props = PREFS;
         props.readPrefs();
         setWin.setSaveConfigSelected(false);
         previousProjectRoot = findRootInPath(dir, props);
      }
         
      if (previousProjectRoot.length() > 0) {        
         configProjectFromFile(previousProjectRoot, props);
      }
   }
   
   private String findRootByFile(String dir, String file) {
      File newFile = new File(dir);
      String searched = F_SEP + file;
      String newFileStr = dir + searched;
      boolean exists = new File(newFileStr).exists();
      while(!exists) {
         if (newFile.getParentFile() == null) {
            newFile = null;
            break;
         }
         newFile    = new File(newFile.getParent());
         newFileStr = newFile.getAbsolutePath() + searched;
         exists = new File(newFileStr).exists();
      }
      if (newFile == null) {
         return "";
      }
      else {
         System.out.println(newFile.toString());
         return newFile.toString();
      }
   }

   private String findRootInPath(String dir, Preferences props) { 
      File newFile = new File(dir);
      File project;
      if (projectPath.length() > 0) {
         project = new File(projectPath);
      }
      else {  
         project = new File(props.getProperty("recentProject"));
      }
      String newFileStr = newFile.getPath();
      String projStr = project.getPath();

      boolean isEqual = projStr.equals(newFileStr);
      while(!isEqual) {
         if (newFile.getParentFile() == null) {
            newFileStr = "";
            break;
         }       
         newFile    = new File(newFile.getParent());
         newFileStr = newFile.getAbsolutePath();
         isEqual    = projStr.equals(newFileStr);
      }
      return newFileStr;     
   }
   
   private void configProjectFromFile(String previousRoot, Preferences props) {
      mainFile = props.getProperty("recentMain");
      setWin.displayFile(mainFile);
      
      moduleDir = props.getProperty("recentModule");
      setWin.displayModule(moduleDir);
      
      sourceDir = props.getProperty("recentSourceDir");
      setWin.displaySourcesDir(sourceDir);
      
      execDir = props.getProperty("recentExecDir");
      setWin.displayExecDir(execDir);
      
      projectPath = previousRoot;
      if (props == CONFIG) {
         System.out.println("props is CONFIG");
         storeToPrefs();
      }
   }
   
   private String pathRelToRoot() {
      getTextFieldsInput();

      String dirRelToRoot = "";
      if (sourceDir.length() > 0 & moduleDir.length() == 0) {
         dirRelToRoot += sourceDir;
      }
      else if (sourceDir.length() == 0 & moduleDir.length() > 0) {
         dirRelToRoot += moduleDir;
      }
      else if (sourceDir.length() > 0 & moduleDir.length() > 0) {
         dirRelToRoot += sourceDir + F_SEP + moduleDir;
      }
      
      return dirRelToRoot + F_SEP + mainFile + suffix;
   }      
   
   private void getTextFieldsInput() {
      mainFile = setWin.projectFileIn();
      moduleDir = setWin.moduleIn();
      sourceDir = setWin.sourcesDirIn();
      execDir = setWin.execDirIn();
      args = setWin.argsIn();
      buildName = setWin.buildNameIn();
   }
   
   private boolean storeInputs() {
      boolean canStore = true;
      if (projectPath.length() == 0) {
         JOptions.warnMessageToFront(
               "An entry in the 'Project' panel is incorrect");
         
         canStore = false;
      }
      else {
         storeToPrefs();
         
         if (setWin.isSaveConfig()) {
            CONFIG.storeConfig("recentMain", mainFile, projectPath);
            CONFIG.storeConfig("recentModule", moduleDir, projectPath);
            CONFIG.storeConfig("recentSourceDir", sourceDir, projectPath);
            CONFIG.storeConfig("recentExecDir", execDir, projectPath);
         }
         else {
            File configFile = new File(projectPath + F_SEP + CONFIG_FILE);
            if (configFile.exists()) {
               int res = JOptions.confirmYesNo(
                       "'Save settings in project folder' is disabled."
                     + " Remove the 'config' file?");
               if (res == 0) {
                  boolean success = configFile.delete();
                  if (!success) {
                     JOptions.warnMessage(
                           "Deleting the 'config' file failed");
                  }
               }
            }
         }
      }
      return canStore;
   }
   
   private void storeToPrefs() {
      PREFS.storePrefs("recentProject", projectPath);
      PREFS.storePrefs("recentMain", mainFile);
      PREFS.storePrefs("recentModule", moduleDir);
      PREFS.storePrefs("recentSourceDir", sourceDir);
      PREFS.storePrefs("recentExecDir", execDir);
   }
}