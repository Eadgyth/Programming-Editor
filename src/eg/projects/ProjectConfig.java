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
   private final static Preferences EAD_CONFIG = new Preferences();

   private final String suffix;

   private SettingsWin setWin = null;
   private String projectRoot = "";
   private String mainFile = "";
   private String moduleDirName = "";
   private String execDirName = "";
   private String sourceDirName = "";
   private String args = "";
   private String buildName = "";
   private String projTestName = "";

   /**
    * @param suffix  the file extension of source files
    * Includes the dot (e.g. .java)
    */
   protected ProjectConfig(String suffix) {
      this.suffix = suffix;
   }

   /**
    * Creates a {@code SettingsWin} with the basic content
    * @see SettingsWin#basicWindow(String)
    */
   @Override
   public void createSettingsWin() {
      SettingsWin win = SettingsWin.basicWindow("Name of main project file");
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
      projectRoot = findRootByFile(dir, pathRelToRoot(true));
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
      String root = findRootByFile(dir, Preferences.CONFIG_FILE);
      if (root.length() > 0) {
         EAD_CONFIG.readConfig(root);
         setWin.setSaveConfigSelected(true);
         configByPropertiesFile(root, EAD_CONFIG);
      }
      else {
         PREFS.readPrefs();
         setWin.setSaveConfigSelected(false);
         root = PREFS.getProperty("recentProject");
         if (isInProject(dir, root)) {
             configByPropertiesFile(root, PREFS);
         }
      }
      return projectRoot.length() > 0;
   }

   @Override
   public boolean isInProject(String dir) {
      return isInProject(dir, projectRoot);
   }

   @Override
   public String getProjectPath() {
      return projectRoot;
   }

   @Override
   public String getProjectName() {
      File f = new File(projectRoot);
      return f.getName();
   }

   @Override
   public String getExecutableDirName() {
      return execDirName;
   }

   @Override
   public String getSourceSuffix() {
      return suffix;
   }

   @Override
   public void storeInPrefs() {
      PREFS.storePrefs("recentProject", projectRoot);
      PREFS.storePrefs("recentMain", mainFile);
      PREFS.storePrefs("recentModule", moduleDirName);
      PREFS.storePrefs("recentSourceDir", sourceDirName);
      PREFS.storePrefs("recentExecDir", execDirName);
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
      return moduleDirName;
   }

   /**
    * Returns the name of the directoy where source files are saved
    *
    * @return  the name of the directory where source files are saved
    */
   protected String getSourceDirName() {
      return sourceDirName;
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
      File f = new File(projectRoot + F_SEP + execDirName + F_SEP
             + moduleDirName + F_SEP + mainFile + aSuffix);
      return f.exists();
   }

   //
   //--private--//
   //

   private void configByPropertiesFile(String root, Preferences prefs) {
      mainFile = prefs.getProperty("recentMain");
      setWin.displayFile(mainFile);
      moduleDirName = prefs.getProperty("recentModule");
      setWin.displayModule(moduleDirName);
      sourceDirName = prefs.getProperty("recentSourceDir");
      setWin.displaySourcesDir(sourceDirName);
      execDirName = prefs.getProperty("recentExecDir");
      setWin.displayExecDir(execDirName);
      buildName = prefs.getProperty("recentBuildName");
      setWin.displayBuildName(buildName);

      File fToTest = new File(root + F_SEP + pathRelToRoot(false));
      if (fToTest.exists()) {
         projectRoot = root;
         setWin.displayProjDirName(getProjectName());
         if (prefs == EAD_CONFIG) {
            storeInPrefs();
         }
      }
   }
   
   private boolean isInProject(String dir, String projRoot) {
      File child = new File(dir);
      File root = new File(projRoot);
      while(child != null) {
         if (child.equals(root)) {
            return true;
         }
         child = child.getParentFile();
      }
      return false;
   }
   
   private String findRootByFile(String dir, String file) {
      File root = new File(dir);
      String relToRoot = F_SEP + file;
      String existingPath = null;
      while (root != null) {
         existingPath = root.getPath() + relToRoot;
         if (new File(existingPath).exists()) {
            return root.getPath();
         }
         root = root.getParentFile();
      }
      return "";
   }

   private String pathRelToRoot(boolean bySetWin) {
      if (bySetWin) {
         getTextFieldsInput();
      }
      StringBuilder sb = new StringBuilder();
      if (sourceDirName.length() > 0) {
         sb.append(sourceDirName);
         sb.append(F_SEP);
      }
      if (moduleDirName.length() > 0) {
         sb.append(moduleDirName);
         sb.append(F_SEP);
      }
      sb.append(mainFile + suffix);
      return sb.toString();
   }

   private void getTextFieldsInput() {
      projTestName = setWin.projDirNameInput();
      mainFile = setWin.projectFileNameInput();
      moduleDirName = setWin.moduleNameInput();
      sourceDirName = setWin.sourcesDirNameInput();
      execDirName = setWin.execDirNameInput();
      args = setWin.argsInput();
      buildName = setWin.buildNameInput();
   }

   private boolean storeInputs() {
      boolean canStore = projectRoot.length() > 0;
      if (canStore && projTestName.length() == 0) {
         setWin.displayProjDirName(getProjectName());
      }
      else if (canStore && projTestName.length() > 0) {
         canStore = projTestName.equals(getProjectName());
      }
      if (!canStore) {
         JOptions.warnMessageToFront(
               "An entry in the 'Structure' panel is incorrect");
      }
      else {
         storeInPrefs();
         if (setWin.isSaveConfig()) {
            EAD_CONFIG.storeConfig("recentMain", mainFile, projectRoot);
            EAD_CONFIG.storeConfig("recentModule", moduleDirName, projectRoot);
            EAD_CONFIG.storeConfig("recentSourceDir", sourceDirName, projectRoot);
            EAD_CONFIG.storeConfig("recentExecDir", execDirName, projectRoot);
            EAD_CONFIG.storeConfig("recentBuildName", buildName, projectRoot);
         }
         else {
            deleteConfigFile();
         }
      }
      return canStore;
   }
      
   private void deleteConfigFile() {
      File configFile = new File(projectRoot + F_SEP
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
