package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.JOptions;

/**
 * Represents the configuration of a project.
 * <p>
 * Class works in combination with {@link SettingsWin} where the name of a project
 * file and optionally names of sub-directories and some other properties are
 * entered.
 * <p>
 * The root folder of the project is not specified explicitely in the settings
 * window but the directory of a file that is part of the project (and which is
 * not necessarily the main project file) is passed in as an argument in
 * {@link #configureProject(String)}. It is tested if the main project file whose
 * name is entered in the settings window exists in this directory or possibly
 * further upwards in the path of this directory. The main project file may also be
 * found in sub-directories relative to the supposed root. These must be specified
 * in the settings window and have the order 'sourcesDirName'/'moduleName' if both
 * of these properties are specified. However, the name of the root may be entered
 * in the settings window to require that the found root directory has this name.
 * <p>
 * The configuration of a project is stored in the prefs file of the program and
 * optionally in a 'config' file that is saved in the project's root folder. A
 * stored project may be retrieved using {@link #retrieveProject(String)} in which
 * the directory of any file that is part of the project may be passed in.
 */
public abstract class ProjectConfig implements Configurable {

   private final static String F_SEP = File.separator;
   /*
    * Used to read prefs from the program's Prefs file */
   private final static Preferences PREFS = new Preferences();
   /*
    * Used to read prefs from an 'eadconfig' file that may be saved in a project */
   private final static Preferences CONFIG = new Preferences();

   private final String suffix;

   private SettingsWin setWin = null;
   private String projTestName = "";
   private String projectPath = "";
   private String mainFile = "";
   private String moduleDir = "";
   private String execDir = "";
   private String sourceDir = "";
   private String args = "";
   private String buildName = "";

   /**
    * @param suffix  the file extension of source files that also
    * represents the type of project.
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
    * {@inheritDoc}
    * <p>Method first looks for an 'eadconfig' file in <code>dir</code> or in
    * parents of it and, if this is not present, in the preferences file in
    * the program folder.
    */
   @Override
   public boolean retrieveProject(String dir) {
      findSavedProject(dir);
      return projectPath.length() > 0;
   }

   @Override
   public boolean isInProject(String dir) {
      return isInProject(dir, projectPath);
   }

   @Override
   public String getProjectPath() {
      return projectPath;
   }

   @Override
   public String getProjectName() {
      File f = new File(projectPath);
      return f.getName();
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
    * Sets a <code>SettingsWin</code> object
    *
    * @param setWin  a {@link SettingsWin} object
    */
   protected void setSettingsWin(SettingsWin setWin) {
      if (this.setWin != null) {
         throw new IllegalStateException("A SettingsWin"
               + " is already set cannot be replaced.");
      }
      this.setWin = setWin;
   }

   /**
    * Returns the name of the project's main file
    *
    * @return  the name of project's main file
    */
   protected String getMainFile() {
      return mainFile;
   }

   /**
    * Returns the name of the directory of a module.
    *
    * @return  the name of the directory of module
    */
   protected String getModuleName() {
      return moduleDir;
   }

   /**
    * Returns the name of the directoy where source files are saved
    *
    * @return  the name of the directory where source files are saved
    */
   protected String getSourceDirName() {
      return sourceDir;
   }

   /**
    * Returns the name for a build
    *
    * @return  the name for a build
    */
   protected String getBuildName() {
      return buildName;
   }

   /**
    * Returns the arguments for a start script
    *
    * @return  the arguments for a start command
    */
   protected String getArgs() {
      return args;
   }

   /**
    * If the main executable file exists
    *
    * @param aSuffix  the extension of the project's executable file(s)
    * @return  if the main executable file exists
    */
   protected boolean mainExecFileExists(String aSuffix) {
      File f = new File(projectPath + F_SEP + execDir + F_SEP + moduleDir
            + F_SEP + mainFile + aSuffix);
      return f.exists();
   }

   //
   //--private--//
   //

   private void findSavedProject(String dir) {
      //
      // first look if there is a eadconfig file
      String root = findRootByFile(dir, Preferences.CONFIG_FILE);
      boolean found = root.length() > 0;
      if (found) {
         CONFIG.readConfig(root);
         setWin.setSaveConfigSelected(true);
         configProjectByProps(root, CONFIG);
      }
      //
      // look if the path includes the project root stored in prefs
      else {
         PREFS.readPrefs();
         setWin.setSaveConfigSelected(false);
         root = PREFS.getProperty("recentProject");
         if (isInProject(dir, root)) {
             configProjectByProps(root, PREFS);
         }
      }
   }

   /**
    * Tries to find the project root within the specified path by
    * looking for an existing file that is a child of this root.
    * <code>file</code> may be a file or a (relative) path
    */
   private String findRootByFile(String path, String file) {
      File projPath = new File(path);
      String relToRoot = F_SEP + file;
      String existingPath = null;
      while (projPath != null) {
         existingPath = projPath.getPath() + relToRoot;
         if (new File(existingPath).exists()) {
            return projPath.getPath();
         }
         projPath = projPath.getParentFile();
      }
      return "";
   }

   /**
    * If path is (sub-)child of the project path
    */
   private boolean isInProject(String dir, String projPath) {
      File child = new File(dir);
      File root = new File(projPath);
      while(child != null) {
         if (child.equals(root)) {
            return true;
         }
         child = child.getParentFile();
      }
      return false;
   }

   /**
    * Configures project using entries in properties file
    */
   private void configProjectByProps(String root, Preferences props) {
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

      File fToTest = new File(root + F_SEP + pathRelToRoot(false));
      if (fToTest.exists()) {
         projectPath = root;
         setWin.displayProjDirName(getProjectName());
         if (props == CONFIG) {
            storeInPrefs();
         }
      }
   }

   /**
    * The file path to the main project file relative to the
    * project's root
    */
   private String pathRelToRoot(boolean bySetWin) {
      if (bySetWin) {
         getTextFieldsInput();
      }
      StringBuilder sb = new StringBuilder();
      if (sourceDir.length() > 0) {
         sb.append(sourceDir);
         sb.append(F_SEP);
      }
      if (moduleDir.length() > 0) {
         sb.append(moduleDir);
         sb.append(F_SEP);
      }
      sb.append(mainFile + suffix);
      return sb.toString();
   }

   private void getTextFieldsInput() {
      projTestName = setWin.projDirNameInput();
      mainFile = setWin.projectFileNameInput();
      moduleDir = setWin.moduleNameInput();
      sourceDir = setWin.sourcesDirNameInput();
      execDir = setWin.execDirNameInput();
      args = setWin.argsInput();
      buildName = setWin.buildNameInput();
   }

   private boolean storeInputs() {
      boolean canStore = projectPath.length() > 0;
      if (canStore && projTestName.length() == 0) {
         setWin.displayProjDirName(getProjectName());
      }
      if (canStore && projTestName.length() > 0) {
         canStore = projTestName.equals(getProjectName());
      }
      if (!canStore) {
         JOptions.warnMessageToFront(
               "An entry in the 'Structure' panel is incorrect");
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
            File configFile = new File(projectPath + F_SEP
                  + Preferences.CONFIG_FILE);
            if (configFile.exists()) {
               int res = JOptions.confirmYesNo(
                       "Saving the 'eadconfig' is disabled."
                     + " Remove the config file?");
               if (res == 0) {
                  boolean success = configFile.delete();
                  if (!success) {
                     JOptions.warnMessage(
                           "Deleting the 'eadconfig' file failed");
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
