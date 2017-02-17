package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.JOptions;

/**
 * Represents the configuration of a project.
 * <p>
 * Class works in combination with {@link SettingsWin} where the name of a
 * project file and optionally names of directories and other properties are
 * entered.
 * <p>
 * The root folder of the project is not specified explicitely but is determined
 * based on the entries in the settings window. In the simplest case, for example,
 * the root would be the parent folder of specified project file. If other
 * sub-directories are specified the root would be the parent of the relative path
 * given by the named sub-directories that point to the project file. The relative
 * path to the project file has the order 'sourcesDirName'/'moduleName' if names
 * for both of these properties are specified.
 * <p>
 * It can be queried if any directory, not just the directory of the specified
 * project file, is found in the project's root folder.
 * <p>
 * The configuration of a project is stored in the prefs file of the program and
 * optionally in a 'config' file that is saved in the  project's root folder.
 */
public abstract class ProjectConfig implements Configurable {
   
   private final static Preferences PREFS = new Preferences();
   private final static Preferences CONFIG = new Preferences();
   private final static String F_SEP = File.separator;
   
   private final String suffix;
   
   private SettingsWin setWin = null;  
   private String projectPath = "";
   private String mainFile = "";
   private String moduleDir = "";
   private String execDir = "";
   private String sourceDir = "";
   private String args = "";
   private String buildName = "";
   
   /**
    * @param suffix  the file extension that represents the type of project.
    * Includes the dot (e.g. .java)
    */
   protected ProjectConfig(String suffix) {
      this.suffix = suffix;
   }
   
   /**
    * Creates a {@code SettingsWin} with the basic content.
    * @see SettingsWin#basicWindow(String)
    */
   @Override
   public void createSettingsWin() {
      SettingsWin win = SettingsWin.basicWindow("Name of project file");
      setSettingsWin(win);
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
      projectPath = findRootByFile(dir, pathRelToRoot(true));
      boolean success = storeInputs();
      if (success) {
         setWin.makeVisible(false);
      }
      return success;
   }
   
   /**
    * If a project configuration stored in 'config' or 'prefs' can be
    * retrieved.
    * <p>
    * Method first looks for a config file and, if not present, in the
    * preferences file in the program folder.
    * <p>
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
   public boolean isInProject(String path) {
      return findRootInPath(path, PREFS).length() > 0;
   }
   
   @Override
   public String getProjectPath() {
      return projectPath;
   }
   
   @Override
   public String getExecutableDirName() {
      return execDir;
   }
   
   @Override
   public String getSourceSuffix() {
      return suffix;
   }
   
   @Override
   public void storeInPrefs() {
      PREFS.storePrefs("recentProject", projectPath);
      PREFS.storePrefs("recentMain", mainFile);
      PREFS.storePrefs("recentModule", moduleDir);
      PREFS.storePrefs("recentSourceDir", sourceDir);
      PREFS.storePrefs("recentExecDir", execDir);
      PREFS.storePrefs("recentBuildName", buildName);
   }
   
   /**
    * Sets this {@code SettingsWin}
    * @param setWin  the new {@link SettingsWin}
    */
   protected void setSettingsWin(SettingsWin setWin) {
      if (this.setWin != null) {
         throw new IllegalStateException("A SettingsWin"
               + " is already set cannot be replaced.");
      }
      this.setWin = setWin;
   }
   
   /**
    * Returns the name of the project directory
    * @return  the name of the project directory
    */
   protected String getProjectName() {
      File f = new File(projectPath);
      return f.getName();
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
    * Returns the name of the directoy where source files are saved
    * @return  the name of the directoy where source files are saved
    */ 
   protected String getSourceDirName() {
      return sourceDir;
   }

   /**
    * Returns the name for a build
    * @return  the name for a build
    */ 
   protected String getBuildName() {
      return buildName;
   }
   
   /**
    * Retunrns the String that contains arguments for a start script.
    * @return  the String that contains arguments for a start command
    */ 
   protected String getArgs() {
      return args;
   }
   
   /**
    * If the main executable file exists
    * @param aSuffix  the extension of the project's executable file(s)
    * @return  if the main executable file exists
    */
   protected boolean mainExecFileExists(String aSuffix) { 
      File target
            = new File(projectPath + F_SEP + execDir + F_SEP + moduleDir
            + F_SEP + mainFile + aSuffix);
      return target.exists();
   }
   
   //
   //--private--//
   //

   private void findSavedProject(String path) {
      Preferences props = null;
      //
      // firstly look if there is a eadconfig file...
      String root = findRootByFile(path, Preferences.CONFIG_FILE);
      if (root.length() > 0) {
         props = CONFIG;
         props.readConfig(root);
         setWin.setSaveConfigSelected(true);
      }
      //
      // ... if not successful look if the dir includes the project root
      // stored in prefs
      else {
         props = PREFS;
         props.readPrefs();
         setWin.setSaveConfigSelected(false);
         root = findRootInPath(path, props);
      }
      //
      // read in props and set text fields in this SettingsWin  
      if (root.length() > 0) {        
         configProjectByProps(root, props);
      }
   }
   
   /**
    * Tries to find the project root in the specified path by
    * looking for an existing file that is a child of this root.
    * 'file' may be a file or itself a (relative) path
    */
   private String findRootByFile(String path, String file) {
      File searched = new File(path);
      String relToRootStr = F_SEP + file;
      String existingPath = path + relToRootStr;
      boolean exists = new File(existingPath).exists();
      while(!exists) {
         if (searched.getParentFile() == null) {
            searched = null;
            break;
         }
         searched     = new File(searched.getParent());
         existingPath = searched.getPath() + relToRootStr;
         exists       = new File(existingPath).exists();
      }
      if (searched == null) {
         return "";
      }
      else {
         return searched.getPath();
      }
   }

   private String findRootInPath(String path, Preferences props) { 
      File searched = new File(path);
      File project;
      if (projectPath.length() > 0) {
         project = new File(projectPath);
      }
      else {
         project = new File(props.getProperty("recentProject"));
      }
      String searchedStr = searched.getPath();
      String projStr = project.getPath();
      boolean isEqual = projStr.equals(searchedStr);
      while(!isEqual) {
         if (searched.getParentFile() == null) {
            searchedStr = "";
            break;
         }       
         searched    = new File(searched.getParent());
         searchedStr = searched.getPath();
         isEqual     = projStr.equals(searchedStr);
      }
      return searchedStr;     
   }
   
   private void configProjectByProps(String previousRoot, Preferences props) {
      mainFile = props.getProperty("recentMain");
      setWin.displayFile(mainFile);
      
      moduleDir = props.getProperty("recentModule");
      setWin.displayModule(moduleDir);
      
      sourceDir = props.getProperty("recentSourceDir");
      setWin.displaySourcesDir(sourceDir);
      
      execDir = props.getProperty("recentExecDir");
      setWin.displayExecDir(execDir);

      buildName = props.getProperty("recentBuildName");
      setWin.displayBuildName(buildName);
      
      File fToTest = new File(previousRoot + F_SEP + pathRelToRoot(false));     
      if (fToTest.exists()) {
         projectPath = previousRoot;
         if (props == CONFIG) {
            storeInPrefs();
         }
      }
   }
   
   private String pathRelToRoot(boolean bySetWin) {
      if (bySetWin) {
         getTextFieldsInput();
      }
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
      mainFile = setWin.projectFileNameInput();
      moduleDir = setWin.moduleNameInput();
      sourceDir = setWin.sourcesDirNameInput();
      execDir = setWin.execDirNameInput();
      args = setWin.argsInput();
      buildName = setWin.buildNameInput();
   }
   
   private boolean storeInputs() {
      boolean canStore = projectPath.length() > 0;
      if (!canStore) {
         JOptions.warnMessageToFront(
               "An entry in the 'Project' panel is incorrect");      
      }
      else {
         storeInPrefs();   
         if (setWin.isSaveConfig()) {
            CONFIG.storeConfig("recentMain", mainFile, projectPath);
            CONFIG.storeConfig("recentModule", moduleDir, projectPath);
            CONFIG.storeConfig("recentSourceDir", sourceDir, projectPath);
            CONFIG.storeConfig("recentExecDir", execDir, projectPath);
            CONFIG.storeConfig("recentBuildName", buildName, projectPath);
         }
         else {
            File configFile;
            configFile = new File(projectPath + F_SEP
                     + Preferences.CONFIG_FILE);
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
               else {
                  setWin.setSaveConfigSelected(true);
               }
            }
         }
      }
      return canStore;
   }
}
